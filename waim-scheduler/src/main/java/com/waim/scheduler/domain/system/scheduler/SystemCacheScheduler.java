package com.waim.scheduler.domain.system.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@DependsOn("redisConnectionFactory")
public class SystemCacheScheduler {



}
