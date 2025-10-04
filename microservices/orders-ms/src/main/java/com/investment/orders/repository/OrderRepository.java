package com.investment.orders.repository;

import com.investment.orders.entity.OrderEntity;
import com.investment.orders.utils.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    // Listar órdenes de una cuenta (paginado)
    Page<OrderEntity> findAllByAccountId(UUID accountId, Pageable pageable);

    // Listar órdenes de una cuenta por estado (paginado)
    Page<OrderEntity> findAllByAccountIdAndStatus(UUID accountId, OrderStatusEnum status, Pageable pageable);

    // Obtener una orden garantizando que pertenece a la cuenta (seguridad)
    Optional<OrderEntity> findByOrderIdAndAccountId(UUID orderId, UUID accountId);

    // Consultas por instrumento
    Page<OrderEntity> findAllByInstrumentId(UUID instrumentId, Pageable pageable);
}