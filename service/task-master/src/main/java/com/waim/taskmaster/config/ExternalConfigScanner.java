package com.waim.taskmaster.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.waim.module.config.jasypt"
})
public class ExternalConfigScanner {}
