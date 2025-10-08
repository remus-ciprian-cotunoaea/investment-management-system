package com.investment.accounts.repository;

import com.investment.accounts.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    // CRUD clásico (sin adicionales por ahora)
}