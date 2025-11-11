package com.investment.orders;

import com.investment.orders.configuration.OrdersTopicsProps;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.service.impl.KafkaProducerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class KafkaProducerImplTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    OrdersTopicsProps topics;

    @InjectMocks
    KafkaProducerImpl producer;

    @Test
    void publishOrderCreated_sendsToKafka_withIdAsKey() {
        when(topics.getOrderCreated()).thenReturn("orders.order-created"); // <-- solo este

        var id  = UUID.randomUUID();
        var dto = OrderResponseDto.builder().id(id).build();

        producer.publishOrderCreated(dto);

        verify(kafkaTemplate).send(eq("orders.order-created"), eq(id.toString()), eq(dto));
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void publishTradeExecuted_sendsToKafka_withOrderIdAsKey() {
        when(topics.getTradeExecuted()).thenReturn("trades.trade-executed"); // <-- solo este

        var orderId = UUID.randomUUID();
        var dto = ExecutionResponseDto.builder().orderId(orderId).build();

        producer.publishTradeExecuted(dto);

        verify(kafkaTemplate).send(eq("trades.trade-executed"), eq(orderId.toString()), eq(dto));
        verifyNoMoreInteractions(kafkaTemplate);
    }
}