package com.waim.scheduler.common.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        // 마스터 키 결정 (JVM 옵션 우선 -> 환경 변수)
        String password = resolveMasterKey();

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Jasypt 마스터 키를 찾을 수 없습니다. 실행 옵션이나 환경 변수를 확인하세요.");
        }

        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
        return encryptor;
    }

    private String resolveMasterKey() {
        // 1. 개발 환경용 JVM 시스템 프로퍼티 우선 체크 (-Djasypt.encryptor.password)
        String systemProp = System.getProperty("jasypt.encryptor.password");
        if (systemProp != null && !systemProp.isEmpty()) {
            log.info("Jasypt 마스터 키를 개발용 시스템 프로퍼티(-D)에서 로드합니다.");
            return systemProp;
        }

        // 2. 환경 변수 확인 (WAIM_JASYPT_PASSWORD 또는 JASYPT_PASSWORD)
        String envVar = System.getenv("WAIM_JASYPT_PASSWORD");
        if (envVar == null || envVar.isEmpty()) {
            envVar = System.getenv("JASYPT_PASSWORD");
        }
        
        if (envVar != null && !envVar.isEmpty()) {
             log.info("Jasypt 마스터 키를 환경 변수에서 로드합니다.");
             return envVar;
        }

        // 로컬 테스트용 하드코딩된 패스워드나 다른 수단이 없다면 null 반환
        return null; // 또는 로컬 기본값 설정 고려
    }
}
