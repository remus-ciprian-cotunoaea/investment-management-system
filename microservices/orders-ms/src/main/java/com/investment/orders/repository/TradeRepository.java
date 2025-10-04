package com.investment.orders.repository;

import com.investment.orders.entity.TradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, UUID> {

    // Trades de una orden (paginado)
    Page<TradeEntity> findAllByOrderId(UUID orderId, Pageable pageable);

    // Trades por cuenta (paginado) — útil para el historial del usuario
    Page<TradeEntity> findAllByAccountId(UUID accountId, Pageable pageable);

    // Último trade de una orden (para mostrar estado/exec más reciente)
    Optional<TradeEntity> findTopByOrderIdOrderByExecutedAtDesc(UUID orderId);
}