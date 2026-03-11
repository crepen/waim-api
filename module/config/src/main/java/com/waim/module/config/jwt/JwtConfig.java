package com.waim.module.config.jwt;

import com.waim.module.util.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.time.Duration;
import java.time.OffsetDateTime;

@Configuration
@Slf4j
@Order(10000)
public class JwtConfig {

    @Value("${waim.jwt.secret}")
    private String secret;

    @Value("${waim.jwt.expire_access}")
    private String expire_access;

    @Value("${waim.jwt.expire_refresh}")
    private String expire_refresh;

    @Bean
    public JwtProvider jwtConfig() {

        log.info(">>> LOAD JWT PROVIDER");

        Duration expAccessDur = DurationStyle.SIMPLE.parse(expire_access);
        Duration expRefreshDur = DurationStyle.SIMPLE.parse(expire_refresh);

        return new JwtProvider(
                "WAIM",
                secret,
                expAccessDur,
                expRefreshDur
        );
    }

}
