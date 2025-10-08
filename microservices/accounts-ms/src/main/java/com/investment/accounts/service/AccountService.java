package com.investment.accounts.service;

import com.investment.accounts.model.AccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    // CRUD principal con Model
    AccountModel create(AccountModel account);
    Optional<AccountModel> getById(UUID accountId);
    Page<AccountModel> getAll(Pageable pageable);
    AccountModel update(AccountModel account);
    void delete(UUID accountId);

    // Ciclo de vida útiles
    AccountModel open(UUID accountId);
    AccountModel close(UUID accountId);
}