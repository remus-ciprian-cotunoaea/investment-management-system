package com.investment.positions.repository;

import com.investment.positions.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, UUID> {

    Optional<PositionEntity> findByAccountIdAndInstrumentId(UUID accountId, UUID instrumentId);
}