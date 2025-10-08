package com.investment.accounts.service.impl;

import com.investment.accounts.entity.AccountEntity;
import com.investment.accounts.entity.CashMovementEntity;
import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.model.CashMovementModel;
import com.investment.accounts.repository.AccountRepository;
import com.investment.accounts.repository.CashMovementRepository;
import com.investment.accounts.repository.CurrencyRepository;
import com.investment.accounts.service.SettlementService;
import com.investment.accounts.utils.DateTimeUtils;
import com.investment.accounts.utils.MoneyUtils;
import com.investment.accounts.utils.enums.CashMovementStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final CashMovementRepository cashMovementRepository;
    private final CurrencyRepository currencyRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public CashMovementModel register(CashMovementModel movement) {
        CashMovementEntity e = new CashMovementEntity();

        // Buscar AccountEntity antes de setear
        AccountEntity account = accountRepository.findById(movement.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + movement.accountId()));
        e.setAccountId(account);

        CurrencyEntity c = currencyRepository.findById(movement.currencyId())
                .orElseThrow(() -> new IllegalArgumentException("Moneda no existe: " + movement.currencyId()));
        e.setCurrency(c);

        e.setAmount(MoneyUtils.normalize(movement.amount()));
        e.setType(movement.type());
        e.setStatus(CashMovementStatusEnum.PENDING);
        e.setDate(movement.date() != null ? movement.date() : DateTimeUtils.today());
        e.setNote(movement.note());

        e = cashMovementRepository.save(e);

        // posterior actualización si aplica
        e.setStatus(CashMovementStatusEnum.COMPLETED);
        e = cashMovementRepository.save(e);

        return CashMovementModel.fromEntity(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CashMovementModel> getMovements(UUID accountId, Pageable pageable) {
        return cashMovementRepository.findAllByAccountId(accountId, pageable)
                .map(CashMovementModel::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID accountId) {
        return cashMovementRepository.sumAmountByAccountId(accountId);
    }
}