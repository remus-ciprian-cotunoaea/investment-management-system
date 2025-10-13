package com.investment.positions.configuration;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public HealthIndicator positionsHealthIndicator() {
        return () -> Health.up()
                .withDetail("service", "positions-ms")
                .withDetail("status", "UP")
                .build();
    }

    @Bean
    public WebEndpointProperties webEndpointProperties() {
        WebEndpointProperties properties = new WebEndpointProperties();
        properties.setBasePath("/actuator");
        return properties;
    }
}