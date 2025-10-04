package com.investment.orders.service;

import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.dto.OrderResponseDto;

public interface KafkaProducer {
    void publishOrderCreated(OrderResponseDto payload);
    void publishTradeExecuted(ExecutionResponseDto payload);
}