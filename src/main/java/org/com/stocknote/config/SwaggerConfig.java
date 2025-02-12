package org.com.stocknote.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("StockNote API")
                .version("v1.0.0");

        String securitySchemeName = "JWT";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name("Authorization")            // HTTP Header 이름
                        .type(SecurityScheme.Type.HTTP)   // HTTP 방식
                        .scheme("bearer")                // Bearer 방식
                        .bearerFormat("JWT"));           // JWT 포맷

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
