package com.waim.api.common.config.aspect;

import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RestControllerPerformanceAspect {

    /**
     * Rest controller 실행 시간 측정
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object injectExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // 컨트롤러 메서드 실행

        long executionTime = System.currentTimeMillis() - start;

        if (result instanceof ResponseEntity<?> responseEntity) {
            return ResponseEntity
                    .status(responseEntity.getStatusCode())
                    .headers(responseEntity.getHeaders())
                    .header("X-Server-Execution-Time", String.valueOf(executionTime) + "ms")
                    .body(responseEntity.getBody());
        }

        return result;
    }
}
