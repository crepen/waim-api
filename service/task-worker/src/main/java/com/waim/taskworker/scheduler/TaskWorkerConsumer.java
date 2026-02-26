package com.waim.taskworker.scheduler;

import com.waim.taskworker.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskWorkerConsumer {

    private final RedissonClient redissonClient;

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
                System.out.println("Task " + taskId + " is already being processed by another node.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void executeActualTask(String taskId) {
        // 실제 비즈니스 로직 (비동기 수행)
        System.out.println("Processing Task " + taskId + " on Thread " + Thread.currentThread().getName());
        try { Thread.sleep(2000); } catch (InterruptedException e) {} // 작업 시뮬레이션
    }
}
