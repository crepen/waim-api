package com.waim.core.domain.configure.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waim.core.common.util.crypto.CryptoProvider;
import com.waim.core.domain.configure.model.dto.ConfigRedisData;
import com.waim.core.domain.configure.model.entity.GlobalConfigEntity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>Global Config(Redis) 데이터 관리 Service</h3>
 *
 * @apiNote  CircuitBreaker 적용
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalConfigureRedisService {

    private static final String REDIS_GLOBAL_SETTING_KEY_PREFIX = "cache:waim:global_config:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CryptoProvider cryptoProvider;

    /**
     *
     *
     * @param keys
     * @return
     */
    @CircuitBreaker(name = "redisConfigBreaker", fallbackMethod = "redisFallback")
    public Map<String , String> getConfigsFromRedis(List<String> keys){
        List<String> redisKeys = keys.stream().map(this::getRedisKey).toList();
        List<String> values = redisTemplate.opsForValue().multiGet(redisKeys);

        Map<String, String> found = new HashMap<>();
        if (values == null) return found;

        for (int i = 0; i < keys.size(); i++) {
            String json = values.get(i);
            if (json != null) {
                try {
                    ConfigRedisData data = objectMapper.readValue(json, ConfigRedisData.class);
                    if(data.value() != null){
                        found.put(
                                keys.get(i),
                                data.isEncrypt()
                                ? cryptoProvider.decrypt(data.value())
                                        : data.value()
                        );
                    }

                } catch (Exception e) {
                    log.warn("Redis 데이터 파싱 에러: {}", keys.get(i));
                }
            }
        }
        return found;
    }

    @CircuitBreaker(name = "redisConfigBreaker", fallbackMethod = "setRedisFallback")
    public void setConfigToRedis(String key, String value , boolean isEncrypt){
        try {
            ConfigRedisData cacheDto = new ConfigRedisData(value, isEncrypt);
            String json = objectMapper.writeValueAsString(cacheDto);

            String redisKey = this.getRedisKey(key);
            // TODO : 지속 시간 변경 필요 (개발용)
            redisTemplate.opsForValue().set(redisKey, json, Duration.ofSeconds(30));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Global Config Redis 저장 (CircuitBreaker 미연동)
     *
     * @param key
     * @param value
     * @param isEncrypt
     */
    public void setConfigToRedisDirect(String key, String value, boolean isEncrypt) {
        try {
            ConfigRedisData cacheDto = new ConfigRedisData(value, isEncrypt);
            String json = objectMapper.writeValueAsString(cacheDto);
            String redisKey = this.getRedisKey(key);
            // TODO : 지속 시간 변경 필요 (개발용)
            redisTemplate.opsForValue().set(redisKey, json , Duration.ofSeconds(30));
        } catch (Exception e) {
            log.error("Redis 초기화 직접 저장 실패 [{}]: {}", key, e.getMessage());
            throw new RuntimeException(e); // 호출부에서 재시도할 수 있도록 던짐
        }
    }

    private String getRedisKey(String key){
        return REDIS_GLOBAL_SETTING_KEY_PREFIX + key;
    }

    // region ##### FALLBACK METHOD #####

    /**
     * Select Exception Fallback Method
     */
    private Map<String, String> redisFallback(List<String> keys, Throwable t) {
        log.error("Redis 호출 차단됨(Circuit Open). 사유: {}", t.getMessage());
        return Collections.emptyMap();
    }

    /**
     * Insert Exception Fallback Method
     */
    private void setRedisFallback(String key, String rawValue, boolean isEncrypt, Throwable t) {
        log.warn("Redis 서킷 오픈으로 인해 캐시 저장 건너뜀: {}", t.getMessage());
    }

    // endregion ##### FALLBACK METHOD #####


}
