package com.waim.taskmaster;

import com.waim.taskmaster.config.ExternalConfigScanner;
import com.waim.taskmaster.config.ExternalModuleScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.waim.taskmaster"})
@Import({
        ExternalConfigScanner.class ,
        ExternalModuleScanner.class
})
@EnableScheduling
@EnableAsync
public class TaskMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMasterApplication.class, args);
    }

}
