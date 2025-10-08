package com.investment.accounts.configuration;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ActuatorConfig {

    @Bean
    public InfoContributor accountsInfoContributor() {
        return (Info.Builder builder) -> builder.withDetail("module",
                Map.of(
                        "name", "accounts-ms",
                        "description", "Accounts microservice for Investment Management System"
                )
        );
    }
}