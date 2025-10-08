package com.investment.accounts.dto;

import com.investment.accounts.utils.enums.AccountTypeEnum;

import java.util.UUID;

/**
 * Payload para crear/actualizar una cuenta.
 * Campos mínimos: broker, currency, portfolio, accountNumber y type.
 * El status y las fechas se gestionan en el servidor.
 */
public record AccountRequestDto(
        UUID brokerId,
        UUID currencyId,
        UUID portfolioId,
        String accountNumber,
        AccountTypeEnum type
) {}