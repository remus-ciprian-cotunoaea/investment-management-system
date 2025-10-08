package com.investment.accounts.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Respuesta para movimientos de efectivo. */
public record CashMovementResponseDto(
        UUID id,
        UUID accountId,
        UUID currencyId,
        String currencyCode,
        BigDecimal amount,
        com.investment.accounts.utils.enums.CashMovementTypeEnum type,
        com.investment.accounts.utils.enums.CashMovementStatusEnum status,
        OffsetDateTime date,
        String note
) {}