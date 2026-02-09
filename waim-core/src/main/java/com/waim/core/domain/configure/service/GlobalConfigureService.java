package com.waim.core.domain.configure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waim.core.common.util.crypto.CryptoProvider;
import com.waim.core.domain.configure.model.dto.ConfigRedisData;
import com.waim.core.domain.configure.model.entity.GlobalConfigEntity;
import com.waim.core.domain.configure.model.event.UpdateGlobalConfigEvent;
import com.waim.core.domain.configure.repository.GlobalConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;


/**
 * Global Config 관리 Service
 *
 * @author shlee
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalConfigureService {

    private static final String REDIS_GLOBAL_SETTING_KEY_PREFIX = "waim:global_config:";

    private final GlobalConfigRepository globalConfigRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CryptoProvider cryptoProvider;
    private final ObjectMapper objectMapper;
    private final GlobalConfigureRedisService globalConfigureRedisService;

    /**
     * Global Config 조회 (멀티 조회)
     *
     * @param keys key list
     * @return config list
     */
    public Map<String, String> getConfigs(List<String> keys) {

        // 1. Redis에서 데이터 가져오기 (서킷 브레이커 적용)
        Map<String, String> redisConfigs = globalConfigureRedisService.getConfigsFromRedis(keys);
        Map<String, String> resultMap = new HashMap<>(redisConfigs);

        // 2. Redis에 없는 키 식별
        List<String> missingKeys = keys.stream()
                .filter(key -> !resultMap.containsKey(key))
                .toList();

        // 3. DB 보충 및 캐시 업데이트
        if (!missingKeys.isEmpty()) {
            globalConfigRepository.findAllByKeyIn(missingKeys).forEach(entity -> {
                if(entity.getValue() != null){
                    resultMap.put(
                            entity.getKey(),
                            entity.isEncrypt()
                                    ? cryptoProvider.decrypt(entity.getValue())
                                    : entity.getValue()
                    );
                }

                // 캐시 업데이트는 별도 비동기 또는 예외 처리를 통해 메인 로직 보호
                globalConfigureRedisService.setConfigToRedis(entity.getKey() , entity.getValue() , entity.isEncrypt());
            });
        }

        return resultMap;
    }



    /**
     * Global Config 생성/수정
     *
     * @param key Config key
     * @param value Config value
     * @param isEncrypt Value 암호화 여부
     */
    @Transactional
    public void setConfig(String key, String value, boolean isEncrypt) {

        var matchConfig = globalConfigRepository.findByKey(key);

        String inputValue;
        if (isEncrypt) {
            inputValue = cryptoProvider.encrypt(value);
        } else {
            inputValue = value;
        }



        matchConfig.ifPresentOrElse(
                config -> {
                    config.setValue(inputValue);
                    config.setEncrypt(isEncrypt);
                },
                () -> {
                    GlobalConfigEntity entity = GlobalConfigEntity.builder()
                            .key(key)
                            .value(inputValue)
                            .isEncrypt(isEncrypt)
                            .build();
                    globalConfigRepository.save(entity);
                }
        );

        applicationEventPublisher.publishEvent(new UpdateGlobalConfigEvent(key , inputValue , isEncrypt));
    }






    // region ##### Transaction Event #####

    /**
     * Global Config update 시 Redis 동기화
     *
     * @param event 변경 Global config 정보
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void syncRedisGlobalConfigEventHandler(UpdateGlobalConfigEvent event) {
        log.info("Update global config -> REDIS : {} / {}" , event.key() , event.value() );
        try{
            globalConfigureRedisService.setConfigToRedis(event.key() , event.value() , event.isEncrypt());
        }
        catch (Exception _){}

    }


    /**
     * Global Config update 시 Log 생성
     *
     * @param event 변경 Global config 정보
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateGlobalConfigLogEventHandler(UpdateGlobalConfigEvent event) {
        log.info("Insert global config log : {} / {}" , event.key() , event.value());

        // TODO : Log insert 추가
    }


    // endregion ##### Transaction Event #####

}
