package com.waim.api.common.config;

import com.waim.core.common.util.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JwtConfig {

    @Value("${waim.auth.jwt.secret}")

    private String jwtSecret;

    /**
     * Access Token Expiration (Default : 5m)
     */
    @Value("${waim.auth.jwt.expiration:300000}")
    private long tokenExpiration;

    /**
     * Refresh Token Expiration (Default : 7d)
     */
    @Value("${waim.auth.jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;




    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtSecret, tokenExpiration, refreshTokenExpiration);
    }

}
