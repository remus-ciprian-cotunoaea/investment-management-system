package com.investment.accounts.service.impl;

import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.entity.FxRateEntity;
import com.investment.accounts.repository.CurrencyRepository;
import com.investment.accounts.repository.FxRateRepository;
import com.investment.accounts.service.FxService;
import com.investment.accounts.utils.FxUtils;
import com.investment.accounts.utils.MoneyUtils;
import com.investment.accounts.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FxServiceImpl implements FxService {

    private final CurrencyRepository currencyRepository;
    private final FxRateRepository fxRateRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrencyEntity> getCurrencyByCode(String code) {
        return currencyRepository.findAll().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyEntity> getCurrencies(Pageable pageable) {
        return currencyRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FxRateEntity> getLastRate(UUID fromCurrencyId, UUID toCurrencyId) {
        return fxRateRepository.findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(fromCurrencyId, toCurrencyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FxRateEntity> getRates(UUID fromCurrencyId, UUID toCurrencyId, Pageable pageable) {
        return fxRateRepository.findAllByFromCurrencyIdAndToCurrencyId(fromCurrencyId, toCurrencyId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal convert(BigDecimal amount, UUID fromCurrencyId, UUID toCurrencyId) {
        if (NumberUtils.isZeroOrNull(amount) || fromCurrencyId.equals(toCurrencyId))
            return MoneyUtils.normalize(amount);

        BigDecimal rate = getLastRate(fromCurrencyId, toCurrencyId)
                .map(FxRateEntity::getRate)
                .orElseThrow(() -> new IllegalStateException("No FX rate disponible para la pareja"));

        return FxUtils.applyRate(amount, rate);
    }
}