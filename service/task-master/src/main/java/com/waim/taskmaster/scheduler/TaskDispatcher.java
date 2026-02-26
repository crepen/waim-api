package com.waim.taskmaster.scheduler;

import com.waim.module.domain.task.service.TaskService;
import com.waim.module.storage.domain.task.entity.TaskAttributeEntity;
import com.waim.module.storage.domain.task.entity.TaskEntity;
import com.waim.module.storage.domain.task.repository.TaskRepository;
import com.waim.taskmaster.config.RabbitConfig;
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
    private final TaskRepository taskRepository;


    @Scheduled(fixedDelay = 10000)
    @SchedulerLock(name = "master_dispatch_lock" , lockAtMostFor = "9s" , lockAtLeastFor = "5s")
    @Transactional
    public void dispatchTask(){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        List<TaskEntity> taskEntities = taskService.getActiveTask();

        for(TaskEntity taskEntity : taskEntities){
            try{
                Duration delayDuration = DurationStyle.SIMPLE.parse(taskEntity.getIntervalDelay());
                OffsetDateTime nextRun = now.plus(delayDuration);

                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY , taskEntity.getUid());
                log.info("TASK DISPATCH : {} -> {}" , taskEntity.getTaskType() , taskEntity.getUid());

                for(TaskAttributeEntity attr : taskEntity.getTaskAttributes()){
                    log.info(" - {} : {}" , attr.getAttrKey() , attr.getAttrValue());
                }

                taskEntity.setNextRunTimestamp(nextRun);

                taskRepository.save(taskEntity);
            }
            catch (Exception ex){
                log.error("Dispatch Error : {} -> {}", taskEntity.getUid(),ex.getMessage());
            }

        }

    }
}
