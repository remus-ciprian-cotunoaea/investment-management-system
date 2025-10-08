package com.investment.accounts.service.impl;

import com.investment.accounts.entity.AccountEntity;
import com.investment.accounts.entity.BrokerEntity;
import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.model.AccountModel;
import com.investment.accounts.repository.AccountRepository;
import com.investment.accounts.repository.BrokerRepository;
import com.investment.accounts.repository.CurrencyRepository;
import com.investment.accounts.service.AccountService;
import com.investment.accounts.utils.DateTimeUtils;
import com.investment.accounts.utils.enums.AccountStatusEnum;
import com.investment.accounts.utils.enums.AccountTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BrokerRepository brokerRepository;
    private final CurrencyRepository currencyRepository;

    // ===== CRUD principal con Model =====

    @Override
    @Transactional
    public AccountModel create(AccountModel account) {
        AccountEntity e = new AccountEntity();
        e.setPortfolioId(account.portfolioId());
        e.setAccountNumber(account.accountNumber());
        e.setType(account.type() != null ? account.type() : AccountTypeEnum.CASH);
        e.setStatus(account.status() != null ? account.status() : AccountStatusEnum.ACTIVE);
        if (AccountStatusEnum.ACTIVE.equals(e.getStatus())) {
            e.setOpenedAt(DateTimeUtils.now());
        }

        // setear FKs:
        applyBroker(e, account.brokerId());
        applyCurrency(e, account.currencyId());

        return AccountModel.fromEntity(accountRepository.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountModel> getById(UUID accountId) {
        return accountRepository.findById(accountId).map(AccountModel::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountModel> getAll(Pageable pageable) {
        return accountRepository.findAll(pageable).map(AccountModel::fromEntity);
    }

    @Override
    @Transactional
    public AccountModel update(AccountModel account) {
        if (account.id() == null) {
            throw new IllegalArgumentException("Id requerido para update");
        }
        AccountEntity e = accountRepository.findById(account.id())
                .orElseThrow(() -> new IllegalArgumentException("Account no existe: " + account.id()));

        if (account.portfolioId() != null) e.setPortfolioId(account.portfolioId());
        if (account.accountNumber() != null) e.setAccountNumber(account.accountNumber());
        if (account.type() != null) e.setType(account.type());
        if (account.status() != null) e.setStatus(account.status());

        // opened/closed timestamps según status
        if (AccountStatusEnum.ACTIVE.equals(e.getStatus()) && e.getOpenedAt() == null) {
            e.setOpenedAt(DateTimeUtils.now());
        }
        if (AccountStatusEnum.CLOSED.equals(e.getStatus())) {
            e.setClosedAt(DateTimeUtils.now());
        }

        // FKs opcionales
        applyBroker(e, account.brokerId());
        applyCurrency(e, account.currencyId());

        return AccountModel.fromEntity(accountRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(UUID accountId) {
        accountRepository.deleteById(accountId);
    }

    // ===== Ciclo de vida =====

    @Override
    @Transactional
    public AccountModel open(UUID accountId) {
        AccountEntity e = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account no existe: " + accountId));
        e.setStatus(AccountStatusEnum.ACTIVE);
        e.setOpenedAt(DateTimeUtils.now());
        e.setClosedAt(null);
        return AccountModel.fromEntity(accountRepository.save(e));
    }

    @Override
    @Transactional
    public AccountModel close(UUID accountId) {
        AccountEntity e = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account no existe: " + accountId));
        e.setStatus(AccountStatusEnum.CLOSED);
        e.setClosedAt(DateTimeUtils.now());
        return AccountModel.fromEntity(accountRepository.save(e));
    }

    // ===== Helpers (setean en el ENTITY) =====

    private void applyBroker(AccountEntity e, UUID brokerId) {
        if (brokerId == null) {
            e.setBroker(null);
            return;
        }
        BrokerEntity b = brokerRepository.findById(brokerId)
                .orElseThrow(() -> new IllegalArgumentException("Broker no existe: " + brokerId));
        e.setBroker(b);
    }

    private void applyCurrency(AccountEntity e, UUID currencyId) {
        if (currencyId == null) {
            e.setCurrency(null);
            return;
        }
        CurrencyEntity c = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new IllegalArgumentException("Currency no existe: " + currencyId));
        e.setCurrency(c);
    }
}