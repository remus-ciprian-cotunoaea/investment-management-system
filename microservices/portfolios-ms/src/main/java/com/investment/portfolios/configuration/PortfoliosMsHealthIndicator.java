package com.investment.portfolios.configuration;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PortfoliosMsHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "portfolios-ms")
                .withDetail("status", "running")
                .build();
    }
}
