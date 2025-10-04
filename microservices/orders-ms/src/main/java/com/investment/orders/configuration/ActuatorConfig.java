package com.investment.orders.configuration;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ActuatorConfig {

    @Bean
    public InfoContributor ordersInfoContributor() {
        return builder -> builder.withDetails(Map.of(
                "service", "orders-ms",
                "owner", "investment-platform",
                "description", "Gestión de órdenes y ejecución"
        ));
    }
}