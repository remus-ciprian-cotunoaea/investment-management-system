package com.investment.users.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI / Swagger documentation for the Users microservice.
 *
 * <p>Defines the base OpenAPI metadata and security scheme (Bearer JWT) used by
 * the API documentation, and creates grouped API definitions for users and admin
 * endpoints to organize the generated docs.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearer-jwt";

    /**
     * Create the base OpenAPI definition containing API info and the security scheme.
     *
     * <p>This bean configures the API title, version and description and registers
     * an HTTP bearer security scheme named "bearer-jwt" (JWT tokens). The security
     * requirement is applied globally so secured endpoints in the docs will show
     * the lock icon allowing consumers to provide a JWT when trying endpoints.</p>
     *
     * @return configured {@link OpenAPI} instance
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Bean
    OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Users MS API")
                        .version("v1")
                        .description("Operations for users and admin"))
                .components(new Components().addSecuritySchemes(
                        SECURITY_SCHEME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
    }

    /**
     * Create an OpenAPI group for user-facing endpoints.
     *
     * <p>Grouping the API under "users" restricts the generated documentation to
     * paths matching "/api/v1/users/**" which helps separate public user APIs
     * from administrative ones in the documentation UI.</p>
     *
     * @return configured {@link GroupedOpenApi} for user endpoints
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Bean
    GroupedOpenApi usersGroup() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    /**
     * Create an OpenAPI group for administrative endpoints.
     *
     * <p>This group restricts the documentation to admin routes under
     * "/api/v1/admin/**" so administrators can review and test admin APIs
     * separately from regular user APIs.</p>
     *
     * @return configured {@link GroupedOpenApi} for admin endpoints
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Bean
    GroupedOpenApi adminGroup() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/v1/admin/**")
                .build();
    }
}