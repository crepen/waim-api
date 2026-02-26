package com.waim.api.common.config.security;

import com.waim.api.common.config.security.filter.JwtSecurityFilter;
import com.waim.api.common.config.security.handler.SecurityAccessDeniedHandler;
import com.waim.api.common.config.security.handler.SecurityAuthenticationEntryPoint;
import com.waim.core.common.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class PublicAPISecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityAuthenticationEntryPoint authenticationEntryPoint;
    private final SecurityAccessDeniedHandler securityAccessDeniedHandler;

    @Bean
    @Order(8) // 공개 리소스 다음으로 검사
    public SecurityFilterChain apiFilterChain(HttpSecurity http) {
        http
                .securityMatcher(
                        "/api/**",
                        "/**"
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().denyAll())
                .addFilterBefore(new JwtSecurityFilter(jwtTokenProvider ), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(securityAccessDeniedHandler)
                );

        return http.build();
    }


}
