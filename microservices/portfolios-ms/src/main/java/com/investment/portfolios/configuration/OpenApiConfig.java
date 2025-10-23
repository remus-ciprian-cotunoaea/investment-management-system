package com.investment.portfolios.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that exposes an OpenAPI (Swagger) definition for the Portfolios microservice.
 *
 * <p>This configuration provides a pre-configured {@link io.swagger.v3.oas.models.OpenAPI} bean
 * containing basic API metadata such as title, version and description. The API version is
 * resolved at runtime by attempting to read the {@code Implementation-Version} entry from the
 * package MANIFEST; if not available, a default snapshot version is returned.
 *
 * <p>Placing this bean in the Spring context enables automatic generation of OpenAPI metadata
 * consumed by Swagger UI or other OpenAPI-compatible tools.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Configuration
public class OpenApiConfig {

    /**
     * Create and register the OpenAPI bean for the application.
     *
     * <p>The returned {@link OpenAPI} instance includes:
     * - title: "Portfolios MS API"
     * - version: resolved from the package MANIFEST or a snapshot default
     * - description: short description of the microservice REST API
     * This bean is used by tooling (e.g., Springdoc OpenAPI) to generate the API documentation.
     *
     * @return a configured {@link OpenAPI} instance with API metadata
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Bean
    public OpenAPI portfoliosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolios MS API")
                        .version(resolveVersion())
                        .description("REST API for Portfolios microservice"));
    }

    /**
     * Resolve the API version string to expose in the OpenAPI metadata.
     *
     * <p>The method attempts to read the {@code Implementation-Version} from the runtime package
     * (from MANIFEST.MF). If the package or the implementation version is not present or blank,
     * it falls back to the default value {@code "1.0.0-SNAPSHOT"}.
     *
     * <p>Typical use: a build system can populate Implementation-Version during packaging so that
     * the API documentation shows the real artifact version at runtime.
     *
     * @return the resolved version string (non-null, non-blank)
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    private String resolveVersion() {
        // Read Implementation-Version from MANIFEST if available; fallback to SNAPSHOT
        Package pkg = this.getClass().getPackage();
        String v = (pkg != null) ? pkg.getImplementationVersion() : null;
        return (v != null && !v.isBlank()) ? v : "1.0.0-SNAPSHOT";
    }
}