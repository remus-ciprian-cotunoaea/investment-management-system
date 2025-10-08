package com.investment.accounts.service;

import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.entity.FxRateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface FxService {

    // Currencies
    Optional<CurrencyEntity> getCurrencyByCode(String code);
    Page<CurrencyEntity> getCurrencies(Pageable pageable);

    // FX Rates
    Optional<FxRateEntity> getLastRate(UUID fromCurrencyId, UUID toCurrencyId);
    Page<FxRateEntity> getRates(UUID fromCurrencyId, UUID toCurrencyId, Pageable pageable);

    // Conveniencia de dominio (usará FxUtils en la impl)
    BigDecimal convert(BigDecimal amount, UUID fromCurrencyId, UUID toCurrencyId);
}