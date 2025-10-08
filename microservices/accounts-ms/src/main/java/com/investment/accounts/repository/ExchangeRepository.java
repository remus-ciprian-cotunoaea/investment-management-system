package com.investment.accounts.repository;

import com.investment.accounts.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExchangeRepository extends JpaRepository<ExchangeEntity, UUID> {
    // CRUD
}