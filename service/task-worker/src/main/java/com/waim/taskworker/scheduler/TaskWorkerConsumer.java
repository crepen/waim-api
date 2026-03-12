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
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.nio.channels.ClosedChannelException;
import java.util.LinkedHashMap;
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
    private static final String ATTR_SOURCE_HEADERS = "SOURCE_HEADERS";
    private static final String ATTR_TARGET_HEADERS = "TARGET_HEADERS";
    private static final long LOCK_LEASE_SECONDS = 60L;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final RedissonClient redissonClient;
    private final TaskService taskService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME , concurrency = "10-15")
    public void handleTask(String rawPayload){
        DispatchMeta dispatchMeta = parseDispatchMeta(rawPayload);
        String taskId = dispatchMeta.taskUid();

        // 1. Valkey를 이용한 분산 락 획득 (중복 실행 최종 방지)
        // Task 주기가 10초라면 락 유지 시간은 그보다 짧게 설정
        RLock lock = redissonClient.getLock("lock:task:" + taskId);

        try {
            // waitTime: 0 (이미 실행 중이면 즉시 포기), leaseTime: 10s (자동 해제)
            if (lock.tryLock(0, LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                try {
                    executeActualTask(taskId, dispatchMeta);
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

    private void executeActualTask(String taskId, DispatchMeta dispatchMeta) {
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

        if (isLateArrival(dispatchMeta)) {
            long delayedMs = Math.max(0L, Instant.now().toEpochMilli() - dispatchMeta.enqueuedAtMs());
            String lateMessage = "Task 수신 지연으로 실행이 거부되었습니다. queueDelay=" + delayedMs + "ms, allowedDelay=" + dispatchMeta.allowedDelayMs() + "ms";
            taskService.writeRunLog(
                    task.getProject().getUid(),
                    task.getUid(),
                    task.getTaskType(),
                    TaskRunStatus.FAILED,
                    null,
                    System.currentTimeMillis() - startedAt,
                    truncateMessage(lateMessage)
            );
            log.warn("Late task arrival detected. taskId={}, queueDelay={}ms, allowed={}ms", taskId, delayedMs, dispatchMeta.allowedDelayMs());
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
                    truncateMessage(resolveFailureMessage(task, attrs, ex))
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
        Map<String, String> sourceHeaders = parseHeaders(attrs.get(ATTR_SOURCE_HEADERS));

        HttpRequest request = buildRequest(sourceMethod, sourceUrl, null, sourceHeaders);
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
        Map<String, String> sourceHeaders = parseHeaders(attrs.get(ATTR_SOURCE_HEADERS));
        Map<String, String> targetHeaders = parseHeaders(attrs.get(ATTR_TARGET_HEADERS));

        HttpRequest sourceRequest = buildRequest(sourceMethod, sourceUrl, null, sourceHeaders);
        HttpResponse<String> sourceResponse = httpClient.send(sourceRequest, HttpResponse.BodyHandlers.ofString());

        String payload = sourceResponse.body() == null ? "" : sourceResponse.body();
        HttpRequest targetRequest = buildRequest(targetMethod, targetUrl, payload, targetHeaders);
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

    private HttpRequest buildRequest(String method, String url, String body, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30));

        headers.forEach((headerName, headerValue) -> {
            if (StringUtils.hasText(headerName) && StringUtils.hasText(headerValue)) {
                builder.header(headerName, headerValue);
            }
        });

        if (("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method))) {
            String payload = body == null ? "" : body;

            if (!containsHeader(headers, "Content-Type")) {
                builder.header("Content-Type", "application/json");
            }

            return builder
                    .method(method, HttpRequest.BodyPublishers.ofString(payload))
                    .build();
        }

        if ("DELETE".equals(method)) {
            return builder.DELETE().build();
        }

        return builder.GET().build();
    }

    private boolean containsHeader(Map<String, String> headers, String headerName) {
        return headers.keySet().stream().anyMatch(key -> headerName.equalsIgnoreCase(key));
    }

    private Map<String, String> parseHeaders(String rawHeaders) {
        Map<String, String> headers = new LinkedHashMap<>();

        if (!StringUtils.hasText(rawHeaders)) {
            return headers;
        }

        for (String line : rawHeaders.split("\\r?\\n")) {
            if (!StringUtils.hasText(line)) {
                continue;
            }

            String trimmed = line.trim();
            int separatorIndex = trimmed.indexOf(':');

            if (separatorIndex < 0) {
                separatorIndex = trimmed.indexOf('=');
            }

            if (separatorIndex <= 0 || separatorIndex >= trimmed.length() - 1) {
                continue;
            }

            String headerName = trimmed.substring(0, separatorIndex).trim();
            String headerValue = trimmed.substring(separatorIndex + 1).trim();

            if (StringUtils.hasText(headerName) && StringUtils.hasText(headerValue)) {
                headers.put(headerName, headerValue);
            }
        }

        return headers;
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

    private String resolveFailureMessage(TaskEntity task, Map<String, String> attrs, Exception ex) {
        String reason = resolveExceptionReason(ex);

        return switch (task.getTaskType()) {
            case SCHEDULER -> "Scheduler 요청 실패. "
                    + describeEndpoint("source", normalizeMethod(attrs.get(ATTR_SOURCE_METHOD), "GET"), attrs.get(ATTR_SOURCE_URL))
                    + ". 사유: " + reason;
            case API_CRAWLER -> "Interface 요청 실패. "
                    + describeEndpoint("source", normalizeMethod(attrs.get(ATTR_SOURCE_METHOD), "GET"), attrs.get(ATTR_SOURCE_URL))
                    + ", "
                    + describeEndpoint("target", normalizeMethod(attrs.get(ATTR_TARGET_METHOD), "POST"), attrs.get(ATTR_TARGET_URL))
                    + ". 사유: " + reason;
            case API_HOOK -> "Hook 작업 실행 실패. 사유: " + reason;
        };
    }

    private String describeEndpoint(String label, String method, String url) {
        String resolvedUrl = StringUtils.hasText(url) ? url : "(미설정)";
        return label + "=" + method + " " + resolvedUrl;
    }

    private String resolveExceptionReason(Throwable throwable) {
        Throwable current = throwable;

        while (current != null) {
            if (current instanceof ConnectException) {
                return "대상 서버에 연결할 수 없습니다.";
            }

            if (current instanceof HttpTimeoutException) {
                return "요청 시간이 초과되었습니다.";
            }

            if (current instanceof ClosedChannelException) {
                return "연결 채널이 비정상 종료되었습니다.";
            }

            if (StringUtils.hasText(current.getMessage())) {
                return current.getMessage();
            }

            current = current.getCause();
        }

        return throwable.getClass().getSimpleName();
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

    private DispatchMeta parseDispatchMeta(String rawPayload) {
        if (!StringUtils.hasText(rawPayload)) {
            return new DispatchMeta("", Instant.now().toEpochMilli(), 10_000L);
        }

        if (!rawPayload.startsWith("v1:")) {
            return new DispatchMeta(rawPayload, Instant.now().toEpochMilli(), 10_000L);
        }

        String[] parts = rawPayload.split(":", 4);
        if (parts.length < 4) {
            return new DispatchMeta(rawPayload, Instant.now().toEpochMilli(), 10_000L);
        }

        String taskUid = parts[1];
        long enqueuedAtMs;
        long allowedDelayMs;

        try {
            enqueuedAtMs = Long.parseLong(parts[2]);
        } catch (NumberFormatException ex) {
            enqueuedAtMs = Instant.now().toEpochMilli();
        }

        try {
            allowedDelayMs = Math.max(Long.parseLong(parts[3]), 500L);
        } catch (NumberFormatException ex) {
            allowedDelayMs = 10_000L;
        }

        return new DispatchMeta(taskUid, enqueuedAtMs, allowedDelayMs);
    }

    private boolean isLateArrival(DispatchMeta meta) {
        long now = Instant.now().toEpochMilli();
        return now - meta.enqueuedAtMs() > meta.allowedDelayMs();
    }

    private record DispatchMeta(String taskUid, long enqueuedAtMs, long allowedDelayMs) {
    }
}
