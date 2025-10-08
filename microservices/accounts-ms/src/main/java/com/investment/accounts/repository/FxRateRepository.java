package com.investment.accounts.repository;

import com.investment.accounts.entity.FxRateEntity;
import com.investment.accounts.entity.FxRateEntity.FxRateId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FxRateRepository extends JpaRepository<FxRateEntity, FxRateId> {

    Optional<FxRateEntity> findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(UUID fromCurrencyId,
                                                                               UUID toCurrencyId);

    Page<FxRateEntity> findAllByFromCurrencyIdAndToCurrencyId(UUID fromCurrencyId,
                                                              UUID toCurrencyId,
                                                              Pageable pageable);
}