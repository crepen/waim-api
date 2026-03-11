package com.waim.taskworker.scheduler;

import com.waim.module.core.domain.task.model.data.TaskRunStatus;
import com.waim.module.core.domain.task.model.data.TaskStatus;
import com.waim.module.core.domain.task.model.data.TaskType;
import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.task.service.TaskService;
import com.waim.taskworker.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskWorkerConsumer {

    private static final String ATTR_SOURCE_URL = "SOURCE_URL";
    private static final String ATTR_TARGET_URL = "TARGET_URL";
    private static final String ATTR_SOURCE_METHOD = "SOURCE_METHOD";
    private static final String ATTR_TARGET_METHOD = "TARGET_METHOD";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final RedissonClient redissonClient;
    private final TaskService taskService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME , concurrency = "10-15")
    public void handleTask(String taskId){
        // 1. Valkey를 이용한 분산 락 획득 (중복 실행 최종 방지)
        // Task 주기가 10초라면 락 유지 시간은 그보다 짧게 설정
        RLock lock = redissonClient.getLock("lock:task:" + taskId);

        try {
            // waitTime: 0 (이미 실행 중이면 즉시 포기), leaseTime: 10s (자동 해제)
            if (lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                try {
                    executeActualTask(taskId);
                } finally {
                    // 비동기 작업 완료 후 수동 해제 (필요 시)
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                // 이미 다른 노드에서 처리 중인 경우 로그만 남기고 종료
                log.info("Task {} is already being processed by another node.", taskId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void executeActualTask(String taskId) {
        long startedAt = System.currentTimeMillis();

        TaskEntity task = taskService.getTask(taskId).orElse(null);

        if (task == null) {
            log.warn("Task not found. taskId={}", taskId);
            return;
        }

        if (task.getTaskStatus() != TaskStatus.ACTIVE) {
            taskService.writeRunLog(
                    task.getProject().getUid(),
                    task.getUid(),
                    task.getTaskType(),
                    TaskRunStatus.SKIPPED,
                    null,
                    System.currentTimeMillis() - startedAt,
                    "Task is inactive"
            );
            return;
        }

        Map<String, String> attrs = taskService.getTaskAttributesMap(task.getUid());

        try {
            switch (task.getTaskType()) {
                case SCHEDULER -> executeSchedulerTask(task, attrs, startedAt);
                case API_CRAWLER -> executeInterfaceTask(task, attrs, startedAt);
                case API_HOOK -> taskService.writeRunLog(
                        task.getProject().getUid(),
                        task.getUid(),
                        task.getTaskType(),
                        TaskRunStatus.SKIPPED,
                        null,
                        System.currentTimeMillis() - startedAt,
                        "Hook job is reserved and not implemented yet"
                );
            }
        }
        catch (Exception ex) {
            taskService.writeRunLog(
                    task.getProject().getUid(),
                    task.getUid(),
                    task.getTaskType(),
                    TaskRunStatus.FAILED,
                    null,
                    System.currentTimeMillis() - startedAt,
                    ex.getMessage()
            );
            log.error("Task execution failed. taskId={}", taskId, ex);
        }
    }

    private void executeSchedulerTask(TaskEntity task, Map<String, String> attrs, long startedAt) throws Exception {
        String sourceUrl = attrs.get(ATTR_SOURCE_URL);

        if (!StringUtils.hasText(sourceUrl)) {
            throw new IllegalArgumentException("SOURCE_URL is required for scheduler job");
        }

        String sourceMethod = normalizeMethod(attrs.get(ATTR_SOURCE_METHOD), "GET");

        HttpRequest request = buildRequest(sourceMethod, sourceUrl, null);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        taskService.writeRunLog(
                task.getProject().getUid(),
                task.getUid(),
                task.getTaskType(),
                isSuccessResponse(response.statusCode()) ? TaskRunStatus.SUCCESS : TaskRunStatus.FAILED,
                response.statusCode(),
                System.currentTimeMillis() - startedAt,
                truncateMessage(response.body())
        );
    }

    private void executeInterfaceTask(TaskEntity task, Map<String, String> attrs, long startedAt) throws Exception {
        String sourceUrl = attrs.get(ATTR_SOURCE_URL);
        String targetUrl = attrs.get(ATTR_TARGET_URL);

        if (!StringUtils.hasText(sourceUrl) || !StringUtils.hasText(targetUrl)) {
            throw new IllegalArgumentException("SOURCE_URL and TARGET_URL are required for interface job");
        }

        String sourceMethod = normalizeMethod(attrs.get(ATTR_SOURCE_METHOD), "GET");
        String targetMethod = normalizeMethod(attrs.get(ATTR_TARGET_METHOD), "POST");

        HttpRequest sourceRequest = buildRequest(sourceMethod, sourceUrl, null);
        HttpResponse<String> sourceResponse = httpClient.send(sourceRequest, HttpResponse.BodyHandlers.ofString());

        String payload = sourceResponse.body() == null ? "" : sourceResponse.body();
        HttpRequest targetRequest = buildRequest(targetMethod, targetUrl, payload);
        HttpResponse<String> targetResponse = httpClient.send(targetRequest, HttpResponse.BodyHandlers.ofString());

        taskService.writeRunLog(
                task.getProject().getUid(),
                task.getUid(),
                task.getTaskType(),
                isSuccessResponse(sourceResponse.statusCode()) && isSuccessResponse(targetResponse.statusCode())
                        ? TaskRunStatus.SUCCESS
                        : TaskRunStatus.FAILED,
                targetResponse.statusCode(),
                System.currentTimeMillis() - startedAt,
                truncateMessage(targetResponse.body())
        );
    }

    private HttpRequest buildRequest(String method, String url, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30));

        if (("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method))) {
            String payload = body == null ? "" : body;

            return builder
                    .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(payload))
                    .build();
        }

        if ("DELETE".equals(method)) {
            return builder.DELETE().build();
        }

        return builder.GET().build();
    }

    private String normalizeMethod(String method, String fallback) {
        if (!StringUtils.hasText(method)) {
            return fallback;
        }

        return method.trim().toUpperCase();
    }

    private boolean isSuccessResponse(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private String truncateMessage(String message) {
        if (message == null) {
            return null;
        }

        if (message.length() <= 1000) {
            return message;
        }

        return message.substring(0, 1000);
    }
}
