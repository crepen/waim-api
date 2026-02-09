package com.waim.api.common.config.init;

import com.waim.core.domain.configure.repository.GlobalConfigRepository;
import com.waim.core.domain.configure.service.GlobalConfigureRedisService;
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
@Order(101)
public class SyncGlobalConfigInitializer implements ApplicationRunner {


    private final GlobalConfigRepository globalConfigRepository;
    private final GlobalConfigureRedisService globalConfigureRedisService;



    @Override
    @NullMarked
    public void run(ApplicationArguments args)  {
        try{
            var allGlobalConfigList = globalConfigRepository.findAll();

            log.info("Update Global Config to Redis : {} items" , allGlobalConfigList.size());

            for (var config : allGlobalConfigList) {
                // 최대 3번 재시도 로직
                boolean success = false;
                for (int retry = 1; retry <= 3; retry++) {
                    try {
                        // 서킷 브레이커가 없는 메서드 호출
                        globalConfigureRedisService.setConfigToRedisDirect(
                                config.getKey(),
                                config.getValue(),
                                config.isEncrypt()
                        );
                        success = true;
                        break;
                    } catch (Exception ex) {
                        log.warn("Retry {}/3 failed for key {}: {}", retry, config.getKey(), ex.getMessage());
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    }
                }
                if (!success) {
                    log.error("Final failure to sync key: {}", config.getKey());
                }
            }
        }
        catch (Exception ex){
            log.error("Failed Sync global config to redis : {}" , ex.getMessage());
        }

    }
}
