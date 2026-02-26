package com.waim.taskmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
    scanBasePackages = "com.waim",
    exclude = {
        org.redisson.spring.starter.RedissonAutoConfigurationV2.class
    }
)
@EnableJpaRepositories(basePackages = "com.waim.core")
@EntityScan(basePackages = "com.waim.core")
@EnableScheduling
public class TaskMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMasterApplication.class, args);
    }

}
