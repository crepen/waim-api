package com.waim.taskmaster.scheduler;

import com.waim.taskmaster.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskDispatcher {

    private final RabbitTemplate rabbitTemplate;
//    private final TaskRepository taskRepository;


    @Scheduled(fixedDelay = 10000)
    @SchedulerLock(name = "master_dispatch_lock" , lockAtMostFor = "9s" , lockAtLeastFor = "5s")
    public void dispatchTask(){
        OffsetDateTime now = OffsetDateTime.now();






        for(var i=0;i<10;i++){
            String uid = String.format("[%s][%d] %s" , now.toString() , i , UUID.randomUUID().toString());
            log.info("RABBIT_MQ_SEND_MESSAGE : {}" , uid);
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY , uid);
        }


        // 실행 주기(interval)에 도달한 Task 목록 조회
//        List<TaskEntity> tasksToRun = taskRepository.findTasksToExecute(now);
//
//        for (TaskEntity task : tasksToRun) {
//            // RabbitMQ의 특정 Exchange로 Task ID 전송
//            rabbitTemplate.convertAndSend("task.exchange", "task.routing.key", task.getTaskId());
//
//            // 마지막 실행 시간 업데이트
//            task.setLastRunAt(now);
//            taskRepository.save(task);
//
//            System.out.println("Dispatched Task: " + task.getTaskId());
//        }
    }
}
