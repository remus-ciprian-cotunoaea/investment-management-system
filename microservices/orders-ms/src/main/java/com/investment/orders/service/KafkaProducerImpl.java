package com.investment.orders.service;

import com.investment.orders.configuration.OrdersTopicsProps;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.dto.OrderResponseDto;
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
        // la clave debe ser el ID de la orden ya guardada
        String key = requireKey(payload.getId(), "OrderResponseDto.id");
        log.info("Publishing order-created | key={} | topic={}", key, topics.getOrderCreated());
        kafkaTemplate.send(topics.getOrderCreated(), key, payload);
    }

    @Override
    public void publishTradeExecuted(ExecutionResponseDto payload) {
        // ideal: particionar por orderId (mantiene todos los trades de la misma orden juntos)
        UUID chosen = Optional.ofNullable(payload.getOrderId()).orElse(payload.getId());
        String key = requireKey(chosen, "ExecutionResponseDto.orderId/id");
        log.info("Publishing trade-executed | key={} | topic={}", key, topics.getTradeExecuted());
        kafkaTemplate.send(topics.getTradeExecuted(), key, payload);
    }

    private static String requireKey(UUID id, String fieldName) {
        return Objects.requireNonNull(id, fieldName + " must not be null").toString();
    }
}