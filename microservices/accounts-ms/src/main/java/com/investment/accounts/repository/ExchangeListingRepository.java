package com.investment.accounts.repository;

import com.investment.accounts.entity.ExchangeListingEntity;
import com.investment.accounts.entity.ExchangeListingEntity.ExchangeListingId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExchangeListingRepository extends JpaRepository<ExchangeListingEntity, ExchangeListingId> {

    Optional<ExchangeListingEntity> findByIdExchangeIdAndLocalTicker(UUID exchangeId, String localTicker);

    Page<ExchangeListingEntity> findByIdInstrumentId(UUID instrumentId, Pageable pageable);
}