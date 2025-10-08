package com.investment.accounts.repository;

import com.investment.accounts.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BrokerRepository extends JpaRepository<BrokerEntity, UUID> {
    // CRUD / catálogo
}