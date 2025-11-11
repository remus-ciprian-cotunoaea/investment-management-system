package com.investment.orders.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties holder for Kafka topic names used by the orders microservice.
 *
 * <p>Maps properties with prefix {@code app.kafka.topics} to a type-safe record
 * that exposes the configured topic names for use throughout the application.</p>
 *
 * @param orderCreated   the topic name used for order-created events
 * @param tradeExecuted  the topic name used for trade-executed events
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@ConfigurationProperties(prefix = "app.kafka.topics")
public record OrdersTopicsProps(String orderCreated, String tradeExecuted) {}
