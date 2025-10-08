package com.investment.accounts;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.investment.accounts.configuration.AccountsTopicsProps;
import com.investment.accounts.service.impl.AccountsEventProducerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class AccountsEventProducerImplTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    AccountsTopicsProps topics;

    @InjectMocks
    AccountsEventProducerImpl producer;

    @Test
    void publishPositionsRecalculateRequested_shouldSendKafkaEvent() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        String topic = "positions.recalc.requested";
        when(topics.positionsRecalculateRequested()).thenReturn(topic);

        // Act
        producer.publishPositionsRecalculateRequested(accountId);

        // Assert
        verify(topics).positionsRecalculateRequested();
        verify(kafkaTemplate).send(eq(topic), eq(accountId.toString()), eq(accountId));
        verifyNoMoreInteractions(kafkaTemplate, topics);
    }

    @Test
    void publishPositionsRecalculateRequested_withNullId_shouldThrowNPE() {
        // Arrange
        String topic = "positions.recalc.requested";
        when(topics.positionsRecalculateRequested()).thenReturn(topic);

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> producer.publishPositionsRecalculateRequested(null)
        );

        // sólo se consultó el topic (la llamada a send no ocurre)
        verify(topics).positionsRecalculateRequested();
        verifyNoMoreInteractions(kafkaTemplate, topics);
    }
}