package com.waim.taskworker.config;


import com.waim.module.util.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class JwtConfig {

    @Value("${waim.jwt.issuer:waim}")
    private String jwtIssuer;

    @Value("${waim.jwt.secret}")

    private String jwtSecret;

    /**
     * Access Token Expiration (Default : 5m)
     */
    @Value("${waim.jwt.expiration:300000}")
    private long tokenExpiration;

    /**
     * Refresh Token Expiration (Default : 7d)
     */
    @Value("${waim.jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;




    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(
                jwtIssuer,
                jwtSecret,
                Duration.ofMillis(tokenExpiration),
                Duration.ofMillis(refreshTokenExpiration)
        );
    }

}


