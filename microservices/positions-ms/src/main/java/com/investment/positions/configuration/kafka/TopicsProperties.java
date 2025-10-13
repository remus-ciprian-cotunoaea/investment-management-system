package com.investment.positions.configuration.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record TopicsProperties(
        String tradeExecuted,
        String positionsRecalculateRequested
) {}