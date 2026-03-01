package com.waim.module.core.domain.auth;


import com.waim.module.core.domain.user.UserModuleScanner;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
        "com.waim.module.core.domain.auth",
})
@EntityScan(basePackages = {
        "com.waim.module.core.domain.auth"
})
@EnableJpaRepositories(basePackages = {
        "com.waim.module.core.domain.auth"
})
public class AuthModuleScanner {}
