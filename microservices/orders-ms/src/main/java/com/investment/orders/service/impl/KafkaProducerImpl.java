package com.investment.orders.service.impl;

import com.investment.orders.configuration.OrdersTopicsProps;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.service.KafkaProducer;
import com.investment.orders.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrdersTopicsProps topics;

    @Override
    public void publishOrderCreated(OrderResponseDto payload) {
        String key = requireKey(payload.getId(), Constants.KAFKA_ORDER_RESPONSE_ID);
        log.info(Constants.PUBLISHING_ORDER_CREATED, key, topics.getOrderCreated());
        kafkaTemplate.send(topics.getOrderCreated(), key, payload);
    }

    @Override
    public void publishTradeExecuted(ExecutionResponseDto payload) {
        UUID chosen = Optional.ofNullable(payload.getOrderId()).orElse(payload.getId());
        String key = requireKey(chosen, Constants.KAFKA_EXECUTION_RESPONSE_ID);
        log.info(Constants.PUBLISHING_TRADE_EXECUTED, key, topics.getTradeExecuted());
        kafkaTemplate.send(topics.getTradeExecuted(), key, payload);
    }

    private static String requireKey(UUID id, String fieldName) {
        return Objects.requireNonNull(id, fieldName + Constants.NOT_NULL).toString();
    }
}