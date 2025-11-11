package com.investment.orders.configuration;

import com.investment.orders.utils.Constants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Spring configuration that exposes the OpenAPI (Swagger) definition for the orders' microservice.
 *
 * <p>This configuration defines a bean that customizes the OpenAPI Info section using
 * values from {@code com.investment.orders.utils.Constants} (title, description, version and license).</p>
 *
 * <p>The produced {@link OpenAPI} instance is automatically picked up
 * by SpringDoc/OpenAPI integration to populate the API documentation endpoints (e.g. /v3/api-docs).</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and returns an {@link OpenAPI} instance containing
     * the service metadata (title, description, version and license) used by the API documentation.
     *
     * <p>All values are loaded from {@code Constants} to keep documentation text centralised
     * and configurable.</p>
     *
     * @return a configured {@link OpenAPI} instance
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public OpenAPI ordersOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(Constants.OPEN_API_TITLE_INFO)
                        .description(Constants.OPEN_API_DESCRIPTION_INFO)
                        .version(Constants.VERSION_ONE)
                        .license(new License().name(Constants.MIT_LICENSE)));
    }
}