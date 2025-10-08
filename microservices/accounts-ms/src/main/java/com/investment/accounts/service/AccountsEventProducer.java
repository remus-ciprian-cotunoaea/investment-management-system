package com.investment.accounts.service;

import java.util.UUID;

public interface AccountsEventProducer {
    // Evento Kafka para pedir recálculo de posiciones de una cuenta
    void publishPositionsRecalculateRequested(UUID accountId);
}