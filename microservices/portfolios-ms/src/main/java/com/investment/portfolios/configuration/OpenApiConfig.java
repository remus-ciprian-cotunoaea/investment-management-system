package com.investment.portfolios.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI portfoliosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolios MS API")
                        .version(resolveVersion())
                        .description("REST API for Portfolios microservice"));
    }

    private String resolveVersion() {
        // Read Implementation-Version from MANIFEST if available; fallback to SNAPSHOT
        Package pkg = this.getClass().getPackage();
        String v = (pkg != null) ? pkg.getImplementationVersion() : null;
        return (v != null && !v.isBlank()) ? v : "1.0.0-SNAPSHOT";
    }
}