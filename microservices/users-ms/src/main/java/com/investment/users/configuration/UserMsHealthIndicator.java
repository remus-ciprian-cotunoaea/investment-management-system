package com.investment.users.configuration;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("userMs")
public class UserMsHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbc;

    public UserMsHealthIndicator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Health health() {
        try {
            // chequeo muy ligero a la DB
            jdbc.queryForObject("select 1", Integer.class);
            return Health.up().withDetail("db", "ok").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("db", "fail").build();
        }
    }
}