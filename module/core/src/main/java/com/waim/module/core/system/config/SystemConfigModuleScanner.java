package com.waim.module.core.system.config;


import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
        "com.waim.module.core.system.config",
})
@EntityScan(basePackages = {
        "com.waim.module.core.system.config"
})
@EnableJpaRepositories(basePackages = {
        "com.waim.module.core.system.config"
})
public class SystemConfigModuleScanner {}
