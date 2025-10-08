package com.investment.accounts.repository;

import com.investment.accounts.entity.InstrumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstrumentRepository extends JpaRepository<InstrumentEntity, UUID> {

    Optional<InstrumentEntity> findBySymbol(String symbol); // symbol es UNIQUE
}