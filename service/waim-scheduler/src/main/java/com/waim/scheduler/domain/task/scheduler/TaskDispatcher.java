package com.waim.scheduler.domain.task.scheduler;

import com.waim.module.core.domain.task.model.data.TaskRunStatus;
import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.task.service.TaskService;
import com.waim.scheduler.common.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskDispatcher {

    private static final long TARGET_DISPATCH_DELAY_MS = 500L;

    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;
    private final TaskService taskService;

    @Scheduled(fixedDelay = TARGET_DISPATCH_DELAY_MS)
    @SchedulerLock(name = "master_dispatch_lock", lockAtMostFor = "5s", lockAtLeastFor = "50ms")
    @Transactional
    public void dispatchTask() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        int dispatchedCount = 0;
        int failedCount = 0;

        List<TaskEntity> taskEntities = taskService.getLoopActiveTask();
        boolean workerAvailable = hasActiveWorkerConsumers();

        if (!workerAvailable && !taskEntities.isEmpty()) {
            log.warn("Task worker consumer is unavailable. taskCount={}", taskEntities.size());
        }

        for (TaskEntity taskEntity : taskEntities) {
            long startedAt = System.currentTimeMillis();
            OffsetDateTime nextRun = resolveNextRun(now, taskEntity.getIntervalDelay());

            if (!workerAvailable) {
                failedCount += 1;
                taskEntity.setNextRunTimestamp(nextRun);
                String userFriendlyMessage = "활성 Task Worker 소비자가 없어 Job을 실행할 수 없습니다. Task Worker 서비스 상태를 확인해 주세요.";
                log.warn("Dispatch Skipped (no worker): {} -> {}", taskEntity.getUid(), userFriendlyMessage);
                taskService.writeRunLog(
                        taskEntity.getProject().getUid(),
                        taskEntity.getUid(),
                        taskEntity.getTaskType(),
                        TaskRunStatus.FAILED,
                        null,
                        System.currentTimeMillis() - startedAt,
                        truncateMessage(userFriendlyMessage)
                );
                continue;
            }

            // RabbitMQ 전송 성공/실패와 별개로 다음 시도 시각을 기록해 UI에서 다음 실행을 식별 가능하게 유지한다.
            taskEntity.setNextRunTimestamp(nextRun);

            try {
                String payload = buildDispatchPayload(taskEntity, startedAt);
                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, payload);
                log.info("TASK DISPATCH : {} -> {}", taskEntity.getTaskType(), taskEntity.getUid());
                dispatchedCount += 1;
            } catch (Exception ex) {
                failedCount += 1;
                String userFriendlyMessage = buildDispatchFailureMessage(ex);
                log.error("Dispatch Error : {} -> {}", taskEntity.getUid(), userFriendlyMessage);
                taskService.writeRunLog(
                        taskEntity.getProject().getUid(),
                        taskEntity.getUid(),
                        taskEntity.getTaskType(),
                        TaskRunStatus.FAILED,
                        null,
                        System.currentTimeMillis() - startedAt,
                        truncateMessage(userFriendlyMessage)
                );
            }

        }

        if (!taskEntities.isEmpty()) {
            log.info("Task dispatch summary. total={}, dispatched={}, failed={}", taskEntities.size(), dispatchedCount, failedCount);
        }
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

    private OffsetDateTime resolveNextRun(OffsetDateTime baseTime, String intervalDelay) {
        try {
            Duration delayDuration = DurationStyle.SIMPLE.parse(intervalDelay);
            return baseTime.plus(delayDuration);
        } catch (Exception ex) {
            log.warn("Invalid interval delay. task schedule fallback to +10s. interval={}", intervalDelay);
            return baseTime.plusSeconds(10);
        }
    }

    private String buildDispatchFailureMessage(Exception ex) {
        if (isRabbitUnavailable(ex)) {
            return "RabbitMQ 서버에 연결할 수 없어 Job 전송에 실패했습니다. RabbitMQ 가동 상태와 연결 설정(host/port/계정)을 확인해 주세요.";
        }

        String rootMessage = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        return "Job 전송 중 오류가 발생했습니다: " + rootMessage;
    }

    private boolean isRabbitUnavailable(Throwable throwable) {
        Throwable current = throwable;

        while (current != null) {
            String className = current.getClass().getName().toLowerCase();
            String message = current.getMessage() == null ? "" : current.getMessage().toLowerCase();

            if (className.contains("amqpconnectexception")
                    || className.contains("connectexception")
                    || message.contains("connection refused")
                    || message.contains("failed to connect")) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private boolean hasActiveWorkerConsumers() {
        try {
            Properties queueProps = amqpAdmin.getQueueProperties(RabbitConfig.QUEUE_NAME);
            if (queueProps == null) {
                return false;
            }

            Object consumerCountObj = queueProps.get(RabbitAdmin.QUEUE_CONSUMER_COUNT);
            if (consumerCountObj instanceof Number count) {
                return count.intValue() > 0;
            }

            return false;
        } catch (Exception ex) {
            log.warn("Failed to inspect RabbitMQ consumer count: {}", ex.getMessage());
            return false;
        }
    }

    private String buildDispatchPayload(TaskEntity taskEntity, long enqueuedAtMs) {
        long allowedDelayMs = resolveAllowedDelayMs(taskEntity.getIntervalDelay());
        return "v1:" + taskEntity.getUid() + ":" + enqueuedAtMs + ":" + allowedDelayMs;
    }

    private long resolveAllowedDelayMs(String intervalDelay) {
        try {
            return Math.max(DurationStyle.SIMPLE.parse(intervalDelay).toMillis(), 500L);
        } catch (Exception ex) {
            return 10_000L;
        }
    }
}
