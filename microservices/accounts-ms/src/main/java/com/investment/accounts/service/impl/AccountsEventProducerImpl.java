package com.investment.accounts.service.impl;

import com.investment.accounts.configuration.AccountsTopicsProps;
import com.investment.accounts.service.AccountsEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountsEventProducerImpl implements AccountsEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountsTopicsProps topics;

    @Override
    public void publishPositionsRecalculateRequested(UUID accountId) {
        kafkaTemplate.send(topics.positionsRecalculateRequested(), accountId.toString(), accountId);
    }
}