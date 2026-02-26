package com.waim.api.common.config.init;

import com.waim.core.module.redis.service.CommonRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(100)
public class ClearRedisCacheRunner implements ApplicationRunner {


    private final CommonRedisService commonRedisService;

    @Override
    @NullMarked
    public void run(ApplicationArguments args)  {
        log.info("기동 시 캐시 초기화 시작...");
        // 동기 메소드 호출: 삭제가 완료될 때까지 다음 Runner로 넘어가지 않음
//        commonRedisService.safeClearCacheSync("cache:*");
        log.info("기동 시 캐시 초기화 완료.");
    }
}
