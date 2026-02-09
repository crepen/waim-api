package com.waim.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.waim")
@EnableJpaRepositories(basePackages = "com.waim.core")
@EntityScan(basePackages = "com.waim.core")
@EnableScheduling
public class WaimSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaimSchedulerApplication.class, args);
    }

}
