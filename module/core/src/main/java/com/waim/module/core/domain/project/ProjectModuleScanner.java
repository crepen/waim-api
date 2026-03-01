package com.waim.module.core.domain.project;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
        "com.waim.module.core.domain.project",
})
@EntityScan(basePackages = {
        "com.waim.module.core.domain.project"
})
@EnableJpaRepositories(basePackages = {
        "com.waim.module.core.domain.project"
})
public class ProjectModuleScanner {}
