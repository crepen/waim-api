package com.waim.module.core.domain.task;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@Configuration
@ComponentScan(basePackages = {
        "com.waim.module.core.domain.task"
})
@EntityScan(basePackages = {
        "com.waim.module.core.domain.task"
})
@EnableJpaRepositories(basePackages = {
        "com.waim.module.core.domain.task"
})
public class TaskModuleScanner {}
