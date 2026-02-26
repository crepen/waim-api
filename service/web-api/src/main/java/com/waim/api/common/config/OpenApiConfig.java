package com.waim.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("WAIM API")
                        .version("1.0.0")
                        .description("WAIM REST API Server")
                        .contact(new Contact()
                                .name("WAIM Team")
                                .email("team@waim.com")
                                .url("https://waim.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                                        .scheme("bearer")               // bearer 인증
                                        .bearerFormat("JWT")));

    }
}
