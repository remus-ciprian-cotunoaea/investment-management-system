package com.investment.accounts.repository;

import com.investment.accounts.entity.PriceEntity;
import com.investment.accounts.entity.PriceEntity.PriceId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PriceRepository extends JpaRepository<PriceEntity, PriceId> {

    Optional<PriceEntity> findTopByIdInstrumentIdOrderByIdTsDesc(UUID instrumentId);

    Page<PriceEntity> findByIdInstrumentId(UUID instrumentId, Pageable pageable);
}