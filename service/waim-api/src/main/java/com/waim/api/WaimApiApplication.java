package com.waim.api;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"com.waim"})
@EnableJpaRepositories(basePackages = "com.waim.core")
@EntityScan(basePackages = "com.waim.core")
@EnableAsync
public class WaimApiApplication {

    @PostConstruct
    public void started() {
        // 애플리케이션의 기본 타임존을 서울로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(WaimApiApplication.class, args);
    }

}
