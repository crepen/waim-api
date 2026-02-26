package com.waim.core.module.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonRedisService {

    // 1. RedisTemplate에 제네릭 <String, Object> 적용 (타입 안정성 확보)
    private final RedisTemplate<String, Object> redisTemplate;


    @Async
    public void safeClearCacheAsync(String keyPattern) {
        log.info("비동기 캐시 삭제 시작: {}", keyPattern);
        this.safeClearCacheSync(keyPattern);
    }

    @Async
    public void safeClearCacheSync(String keyPattern) {
        // 2. RedisCallback에 명시적 타입 지정으로 확인되지 않은 호출 경고 해결
        redisTemplate.execute((RedisCallback<Void>) connection -> {

            // 매개변수로 받은 keyPattern을 사용하도록 수정
            ScanOptions options = ScanOptions.scanOptions()
                    .match(keyPattern)
                    .count(100)
                    .build();

            // 3. try-with-resources를 통한 Cursor 리소스 자동 해제
            try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                while (cursor.hasNext()) {
                    byte[] key = cursor.next();
                    connection.keyCommands().del(key);
                }
            } catch (Exception e) {
                log.error("Redis SCAN 중 오류 발생: {}", e.getMessage());
            }
            return null;
        });
    }
}