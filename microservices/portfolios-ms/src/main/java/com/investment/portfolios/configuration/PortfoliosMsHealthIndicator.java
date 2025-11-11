package com.investment.portfolios.configuration;

import com.investment.portfolios.utils.Constants;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


/**
 * Health indicator for the Portfolios microservice.
 *
 * <p>This component implements Spring Boot's {@link HealthIndicator}
 * to provide a simple "up" health status for the service. The returned {@link Health} contains
 * basic metadata that can be exposed by Actuator endpoints (for example, /actuator/health).
 * The health details include:
 * - "service": logical name of the service ("portfolios-ms")
 * - "status": human-readable status ("running")
 * This indicator is intentionally simple and always reports UP. In the future, it can be
 * extended to include downstream checks (databases, external APIs, etc.) and return a more
 * detailed health status.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Component
public class PortfoliosMsHealthIndicator implements HealthIndicator {

    /**
     * Produce the current health status for the Portfolios microservice.
     *
     * <p>Returns a {@link Health#up()} status with additional details:
     * - "service": the service identifier ("portfolios-ms")
     * - "status": a short, human-readable state ("running")
     * Because this implementation always returns UP, it signals the application is running.
     * Replace or extend this method to perform real readiness/liveness checks when needed.
     *
     * @return a {@link Health} instance representing the current health (always UP in this implementation)
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public Health health() {
        return Health.up()
                .withDetail(Constants.SERVICE, Constants.MICROSERVICE_NAME)
                .withDetail(Constants.STATUS, Constants.RUNNING)
                .build();
    }
}