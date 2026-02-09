package com.waim.api.domain.user.config.security;

import com.waim.api.common.config.security.filter.JwtSecurityFilter;
import com.waim.api.common.config.security.handler.SecurityAccessDeniedHandler;
import com.waim.api.common.config.security.handler.SecurityAuthenticationEntryPoint;
import com.waim.core.common.util.jwt.JwtTokenProvider;
import com.waim.core.domain.user.service.WAIMUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class UserSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityAuthenticationEntryPoint authenticationEntryPoint;
    private final SecurityAccessDeniedHandler securityAccessDeniedHandler;


    @Bean
    @Order(5)
    public SecurityFilterChain userApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/user",
                        "/user/**"
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(new JwtSecurityFilter(jwtTokenProvider ), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(securityAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain adminUserApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/admin/user",
                        "/admin/user/**"
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().hasRole("ADMIN")
                )
                .addFilterBefore(new JwtSecurityFilter(jwtTokenProvider ), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(securityAccessDeniedHandler)
                );

        return http.build();
    }
}
