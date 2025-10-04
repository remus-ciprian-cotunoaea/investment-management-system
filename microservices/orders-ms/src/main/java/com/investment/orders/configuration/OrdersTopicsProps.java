package com.investment.orders.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class OrdersTopicsProps {
    @Value("${app.kafka.topics.orderCreated}")
    private String orderCreated;

    @Value("${app.kafka.topics.tradeExecuted}")
    private String tradeExecuted;
}