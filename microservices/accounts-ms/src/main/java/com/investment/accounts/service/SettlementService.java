package com.investment.accounts.service;

import com.investment.accounts.model.CashMovementModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface SettlementService {

    CashMovementModel register(CashMovementModel movement);

    Page<CashMovementModel> getMovements(UUID accountId, Pageable pageable);

    // usa SUM del CashMovementRepository en la implementación
    BigDecimal getBalance(UUID accountId);
}