package com.investment.accounts.model;

import com.investment.accounts.entity.CashMovementEntity;
import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.utils.enums.CashMovementStatusEnum;
import com.investment.accounts.utils.enums.CashMovementTypeEnum;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo de dominio para movimientos de efectivo.
 * Incluye helper desde entidad y proyección a DTO.
 */
public record CashMovementModel(
        UUID id,
        UUID accountId,
        UUID currencyId,
        String currencyCode,
        BigDecimal amount,
        CashMovementTypeEnum type,
        CashMovementStatusEnum status,
        OffsetDateTime date,
        String note
) {
    public static CashMovementModel fromEntity(CashMovementEntity e) {
        CurrencyEntity c = e.getCurrency();
        return new CashMovementModel(
                e.getId(),
                e.getAccountId() != null ? e.getAccountId().getId() : null,
                c != null ? c.getId() : null,
                c != null ? c.getCode() : null,
                e.getAmount(),
                e.getType(),
                e.getStatus(),
                e.getDate(),
                e.getNote()
        );
    }
}