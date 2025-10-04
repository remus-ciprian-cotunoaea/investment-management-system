package com.investment.orders;

import com.investment.orders.configuration.OrdersTopicsProps;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.service.KafkaProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class KafkaProducerImplTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private OrdersTopicsProps topics;

    @InjectMocks
    private KafkaProducerImpl producer;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(topics.getOrderCreated()).thenReturn("orders.order-created");
        when(topics.getTradeExecuted()).thenReturn("trades.trade-executed");
    }

    @Test
    void publishOrderCreated_sendsToKafka_withIdAsKey() {
        var id = UUID.randomUUID();
        var dto = OrderResponseDto.builder()
                .id(id)
                .accountId(UUID.randomUUID())
                .instrumentId(UUID.randomUUID())
                .build();

        producer.publishOrderCreated(dto);

        verify(kafkaTemplate).send(eq("orders.order-created"), eq(id.toString()), eq(dto));
    }

    @Test
    void publishTradeExecuted_sendsToKafka_withIdAsKey() {
        var id = UUID.randomUUID();
        var dto = ExecutionResponseDto.builder()
                .id(id)
                .orderId(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .build();

        producer.publishTradeExecuted(dto);

        verify(kafkaTemplate).send(
                eq("trades.trade-executed"),
                eq(dto.getOrderId().toString()),  // <-- orderId como key
                eq(dto)
        );
    }
}