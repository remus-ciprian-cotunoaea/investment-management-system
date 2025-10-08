package com.investment.accounts.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Respuesta plana y práctica para listar/consultar cuentas. */
public record AccountResponseDto(
        UUID id,
        UUID brokerId,
        String brokerName,
        UUID currencyId,
        String currencyCode,
        UUID portfolioId,
        String accountNumber,
        com.investment.accounts.utils.enums.AccountTypeEnum type,
        com.investment.accounts.utils.enums.AccountStatusEnum status,
        OffsetDateTime openedAt,
        OffsetDateTime closedAt
) {}