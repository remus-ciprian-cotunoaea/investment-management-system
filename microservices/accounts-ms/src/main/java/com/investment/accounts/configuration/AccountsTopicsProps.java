package com.investment.accounts.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record AccountsTopicsProps(
        String tradeExecuted,
        String positionsRecalculateRequested
) {}