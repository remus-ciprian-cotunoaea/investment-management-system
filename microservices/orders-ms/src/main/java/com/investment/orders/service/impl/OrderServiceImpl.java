package com.investment.orders.service.impl;

import com.investment.orders.dto.OrderRequestDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.entity.OrderEntity;
import com.investment.orders.repository.OrderRepository;
import com.investment.orders.service.OrderService;
import com.investment.orders.utils.Constants;
import com.investment.orders.utils.DateTimeUtils;
import com.investment.orders.utils.NumberUtils;
import com.investment.orders.utils.enums.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    // ===== CRUD =====

    @Override
    public OrderResponseDto create(OrderRequestDto request) {
        validateBusiness(request);

        OrderEntity e = new OrderEntity();
        e.setInstrumentId(request.getInstrumentId());
        e.setAccountId(request.getAccountId());
        e.setSide(request.getSide());
        e.setOrderType(request.getOrderType());
        e.setQuantity(roundQ(request.getQuantity()));
        e.setLimitPrice(roundP(request.getLimitPrice()));
        e.setStatus(OrderStatusEnum.PENDING);
        e.setPlacedAt(Instant.now());
        e.setNote(request.getNote());

        OrderEntity saved = repository.save(e);
        return toResponse(saved);
    }

    @Override
    public OrderResponseDto getByIdAndAccountId(UUID orderId, UUID accountId) {
        OrderEntity e = repository.findByOrderIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new IllegalArgumentException(Constants.ORDER_NOT_FOUND));
        return toResponse(e);
    }

    @Override
    public OrderResponseDto update(UUID orderId, UUID accountId, OrderRequestDto request) {
        OrderEntity e = repository.findByOrderIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new IllegalArgumentException(Constants.ORDER_NOT_FOUND));

        if (e.getStatus() != OrderStatusEnum.PENDING) {
            throw new IllegalStateException(Constants.PENDING_UPDATE_ONLY);
        }

        validateBusiness(request);

        e.setSide(request.getSide());
        e.setOrderType(request.getOrderType());
        e.setQuantity(roundQ(request.getQuantity()));
        e.setLimitPrice(roundP(request.getLimitPrice()));
        e.setNote(request.getNote());

        OrderEntity saved = repository.save(e);
        return toResponse(saved);
    }

    @Override
    public void delete(UUID orderId, UUID accountId) {
        OrderEntity e = repository.findByOrderIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new IllegalArgumentException(Constants.ORDER_NOT_FOUND));
        repository.delete(e);
    }

    // ===== List (repo 1:1) =====

    @Override
    public Page<OrderResponseDto> findAllByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Constants.PLACED).descending());
        return repository.findAllByAccountId(accountId, pageable).map(this::toResponse);
    }

    @Override
    public Page<OrderResponseDto> findAllByAccountIdAndStatus(UUID accountId, OrderStatusEnum status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Constants.PLACED).descending());
        return repository.findAllByAccountIdAndStatus(accountId, status, pageable).map(this::toResponse);
    }

    @Override
    public Page<OrderResponseDto> findAllByInstrumentId(UUID instrumentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Constants.PLACED).descending());
        return repository.findAllByInstrumentId(instrumentId, pageable).map(this::toResponse);
    }

    // ===== Helpers =====

    private void validateBusiness(OrderRequestDto r) {       if (r.getQuantity() == null || !NumberUtils.isPositive(r.getQuantity())) {
            throw new IllegalArgumentException(Constants.QUANTITY_GREATER_THAN_ZERO);
        }
        switch (r.getOrderType()) {
            case LIMIT:
            case STOP:
            case STOP_LIMIT:
                if (r.getLimitPrice() == null || !NumberUtils.isZeroOrPositive(r.getLimitPrice())) {
                    throw new IllegalArgumentException(Constants.LIMIT_PRICE_GREATER_THAN_ZERO + r.getOrderType());
                }
                break;
            default:
                // no limitPrice needed
                break;
        }
    }

    private BigDecimal roundQ(BigDecimal v) {          // 28,10
        if (v == null) return null;
        return NumberUtils.round(v, Constants.INT_TEN);
    }

    private BigDecimal roundP(BigDecimal v) {          // 18,6
        if (v == null) return null;
        return NumberUtils.round(v, Constants.INT_SIX);
    }

    private OrderResponseDto toResponse(OrderEntity e) {
        return OrderResponseDto.builder()
                .id(e.getOrderId())
                .instrumentId(e.getInstrumentId())
                .accountId(e.getAccountId())
                .side(e.getSide())
                .orderType(e.getOrderType())
                .quantity(e.getQuantity())
                .limitPrice(e.getLimitPrice())
                .status(e.getStatus())
                .placedAt(DateTimeUtils.toOffsetDateTime(e.getPlacedAt()))
                .note(e.getNote())
                .build();
    }
}