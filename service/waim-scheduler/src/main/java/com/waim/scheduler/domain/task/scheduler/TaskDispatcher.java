package com.waim.scheduler.domain.task.scheduler;

import com.waim.module.core.domain.task.model.data.TaskRunStatus;
import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.task.service.TaskService;
import com.waim.scheduler.common.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskDispatcher {

    private final RabbitTemplate rabbitTemplate;
    private final TaskService taskService;

    @Scheduled(fixedDelay = 10000)
    @SchedulerLock(name = "master_dispatch_lock", lockAtMostFor = "9s", lockAtLeastFor = "5s")
    @Transactional
    public void dispatchTask() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        int dispatchedCount = 0;
        int failedCount = 0;

        List<TaskEntity> taskEntities = taskService.getLoopActiveTask();

        for (TaskEntity taskEntity : taskEntities) {
            long startedAt = System.currentTimeMillis();
            OffsetDateTime nextRun = resolveNextRun(now, taskEntity.getIntervalDelay());

            // RabbitMQ 전송 성공/실패와 별개로 다음 시도 시각을 기록해 UI에서 다음 실행을 식별 가능하게 유지한다.
            taskEntity.setNextRunTimestamp(nextRun);

            try {
                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, taskEntity.getUid());
                log.info("TASK DISPATCH : {} -> {}", taskEntity.getTaskType(), taskEntity.getUid());
                dispatchedCount += 1;
            } catch (Exception ex) {
                failedCount += 1;
                log.error("Dispatch Error : {} -> {}", taskEntity.getUid(), ex.getMessage());
                taskService.writeRunLog(
                        taskEntity.getProject().getUid(),
                        taskEntity.getUid(),
                        taskEntity.getTaskType(),
                        TaskRunStatus.FAILED,
                        null,
                        System.currentTimeMillis() - startedAt,
                        truncateMessage("Dispatch failed: " + ex.getClass().getSimpleName() + " - " + ex.getMessage())
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
}
