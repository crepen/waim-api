package com.waim.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.waim.scheduler")
@EnableScheduling
public class WaimSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaimSchedulerApplication.class, args);
    }

}
