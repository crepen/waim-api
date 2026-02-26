package com.waim.module.domain.task.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
        "com.waim.module.storage.common",
        "com.waim.module.storage.domain.task",
        "com.waim.module.storage.domain.project",
        "com.waim.module.storage.domain.user"
})
@EnableJpaRepositories(basePackages = {
        "com.waim.module.storage.domain.task",
        "com.waim.module.storage.domain.project",
        "com.waim.module.storage.domain.user"
})
public class EnableScanConfig {}
