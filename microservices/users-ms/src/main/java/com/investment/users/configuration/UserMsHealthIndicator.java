package com.investment.users.configuration;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Health indicator for the Users microservice.
 *
 * <p>Provides a lightweight readiness/liveness check by performing a minimal
 * database query. This indicator is exposed under the Actuator health endpoint
 * and reports 'UP' when the database check succeeds or 'DOWN' when it fails.</p>
 *
 * <p>Note: the check is intentionally simple (select 1) to avoid side effects
 * and to provide a fast, low-cost verification that the database is reachable.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Component("users-ms")
public class UserMsHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbc;

    /**
     * Constructor for the health indicator.
     *
     * @param jdbc the {@link JdbcTemplate} to use for the database check
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    public UserMsHealthIndicator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Perform a health check.
     *
     * <p>Attempts a minimal DB query (SELECT 1). If the query completes
     * successfully the method returns {@code Health.up()} with a detail
     * indicating the DB is OK. If any exception occurs the method returns
     * {@code Health.down(throwable)} with a detail indicating the DB failed.</p>
     *
     * @return a {@link Health} instance representing the current health state
     * of the service (UP when DB reachable, DOWN otherwise)
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public Health health() {
        try {
            jdbc.queryForObject("select 1", Integer.class);
            return Health.up().withDetail("db", "ok").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("db", "fail").build();
        }
    }
}