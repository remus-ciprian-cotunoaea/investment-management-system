package com.investment.orders.configuration;

import com.investment.orders.utils.Constants;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * Configuration class that registers Actuator beans related to application metadata.
 *
 * <p>This class exposes an {@link org.springframework.boot.actuate.info.InfoContributor}
 * bean that enriches the Spring Boot Actuator /info endpoint with static metadata
 * about the orders' microservice. The metadata keys and values are sourced from
 * {@code com.investment.orders.utils.Constants}.</p>
 *
 * Usage:
 * - The registered bean is picked up by Spring and contributes the configured details
 *   to the Actuator info endpoint.
 * <p>
 * Example details added:
 * - SERVICE: the microservice name
 * - OWNER: the platform or team owning the service
 * - DESCRIPTION: a short description of the service
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Configuration
public class ActuatorConfig {

    /**
     * Creates an {@link org.springframework.boot.actuate.info.InfoContributor} bean
     * that populates the Actuator /info endpoint with static service metadata.
     *
     * <p>The contributor builds a details map using constants defined in
     * {@code com.investment.orders.utils.Constants}. These entries typically include
     * service name, owner/platform, and a description.</p>
     *
     * @return an InfoContributor that adds service metadata to Actuator's info endpoint
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    public InfoContributor ordersInfoContributor() {
        return builder -> builder.withDetails(Map.of(
                Constants.SERVICE, Constants.MICROSERVICE_NAME,
                Constants.OWNER, Constants.INVESTMENT_PLATFORM,
                Constants.DESCRIPTION, Constants.DESCRIPTION_CONTENT
        ));
    }
}