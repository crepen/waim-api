package com.waim.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.waim"})
@EnableJpaRepositories(basePackages = "com.waim.core")
@EntityScan(basePackages = "com.waim.core")
@EnableAsync
public class WaimApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaimApiApplication.class, args);
    }

}
