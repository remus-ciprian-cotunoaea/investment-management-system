package com.investment.orders.service;

import com.investment.orders.dto.OrderRequestDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.utils.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OrderService {

    // CRUD principal
    OrderResponseDto create(OrderRequestDto request);
    OrderResponseDto getByIdAndAccountId(UUID orderId, UUID accountId);
    OrderResponseDto update(UUID orderId, UUID accountId, OrderRequestDto request);
    void delete(UUID orderId, UUID accountId);

    // Listados que mapean 1:1 a los métodos del repo
    Page<OrderResponseDto> findAllByAccountId(UUID accountId, int page, int size);
    Page<OrderResponseDto> findAllByAccountIdAndStatus(UUID accountId, OrderStatusEnum status, int page, int size);
    Page<OrderResponseDto> findAllByInstrumentId(UUID instrumentId, int page, int size);
}