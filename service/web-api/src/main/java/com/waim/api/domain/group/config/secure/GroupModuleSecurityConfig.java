package com.waim.api.domain.group.config.secure;

import com.waim.api.common.config.security.filter.JwtSecurityFilter;
import com.waim.api.common.config.security.handler.SecurityAccessDeniedHandler;
import com.waim.api.common.config.security.handler.SecurityAuthenticationEntryPoint;
import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.util.jwt.JwtProvider;
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
public class GroupModuleSecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final SecurityAuthenticationEntryPoint authenticationEntryPoint;
    private final SecurityAccessDeniedHandler securityAccessDeniedHandler;

    @Bean
    @Order(5)
    public SecurityFilterChain groupApiSecurityFilterChain(HttpSecurity http) {
        http
                .securityMatcher(
                        "/group",
                        "/group/**",
                        "/api/group",
                        "/api/group/**"
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtSecurityFilter(jwtProvider, userService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(securityAccessDeniedHandler)
                );

        return http.build();
    }
}
