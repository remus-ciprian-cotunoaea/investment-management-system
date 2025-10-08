package com.investment.accounts.model;

import com.investment.accounts.entity.AccountEntity;
import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.entity.BrokerEntity;
import com.investment.accounts.utils.enums.AccountStatusEnum;
import com.investment.accounts.utils.enums.AccountTypeEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo de dominio (sin dependencias de JPA) para cuentas.
 * Incluye helpers para construirlo desde la entidad y para proyectarlo a DTO.
 */
public record AccountModel(
        UUID id,
        UUID brokerId,
        String brokerName,
        UUID currencyId,
        String currencyCode,
        UUID portfolioId,
        String accountNumber,
        AccountTypeEnum type,
        AccountStatusEnum status,
        OffsetDateTime openedAt,
        OffsetDateTime closedAt
) {
    public static AccountModel fromEntity(AccountEntity e) {
        BrokerEntity b = e.getBroker();
        CurrencyEntity c = e.getCurrency();
        return new AccountModel(
                e.getId(),
                b != null ? b.getId() : null,
                b != null ? b.getName() : null,
                c != null ? c.getId() : null,
                c != null ? c.getCode() : null,
                e.getPortfolioId(),
                e.getAccountNumber(),
                e.getType(),
                e.getStatus(),
                e.getOpenedAt(),
                e.getClosedAt()
        );
    }
}