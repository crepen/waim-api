package com.waim.module.core.domain.user;


import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
        "com.waim.module.core.domain.user",
})
@EntityScan(basePackages = {
        "com.waim.module.core.domain.user"
})
@EnableJpaRepositories(basePackages = {
        "com.waim.module.core.domain.user"
})
public class UserModuleScanner {}
