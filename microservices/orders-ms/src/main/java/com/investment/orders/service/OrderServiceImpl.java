package com.investment.orders.service;

import com.investment.orders.dto.OrderRequestDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.entity.OrderEntity;
import com.investment.orders.repository.OrderRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
        return toResponse(e);
    }

    @Override
    public OrderResponseDto update(UUID orderId, UUID accountId, OrderRequestDto request) {
        OrderEntity e = repository.findByOrderIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));

        // regla de ejemplo: sólo PENDING se puede editar
        if (e.getStatus() != OrderStatusEnum.PENDING) {
            throw new IllegalStateException("only PENDING orders can be updated");
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
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
        repository.delete(e);
    }

    // ===== Listados (repo 1:1) =====

    @Override
    public Page<OrderResponseDto> findAllByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("placedAt").descending());
        return repository.findAllByAccountId(accountId, pageable).map(this::toResponse);
    }

    @Override
    public Page<OrderResponseDto> findAllByAccountIdAndStatus(UUID accountId, OrderStatusEnum status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("placedAt").descending());
        return repository.findAllByAccountIdAndStatus(accountId, status, pageable).map(this::toResponse);
    }

    @Override
    public Page<OrderResponseDto> findAllByInstrumentId(UUID instrumentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("placedAt").descending());
        return repository.findAllByInstrumentId(instrumentId, pageable).map(this::toResponse);
    }

    // ===== Helpers =====

    private void validateBusiness(OrderRequestDto r) {
        if (r.getQuantity() == null || !NumberUtils.isPositive(r.getQuantity())) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        // para LIMIT/STOP etc. espera limitPrice; para MARKET lo puedes permitir null
        switch (r.getOrderType()) {
            case LIMIT:
            case STOP:
            case STOP_LIMIT:
                if (r.getLimitPrice() == null || !NumberUtils.isZeroOrPositive(r.getLimitPrice())) {
                    throw new IllegalArgumentException("limitPrice must be >= 0 for " + r.getOrderType());
                }
                break;
            default:
                // MARKET/OTHER no obligamos limitPrice
        }
    }

    private BigDecimal roundQ(BigDecimal v) {          // 28,10 en el modelo
        if (v == null) return null;
        return NumberUtils.round(v, 10);
    }

    private BigDecimal roundP(BigDecimal v) {          // 18,6 en el modelo
        if (v == null) return null;
        return NumberUtils.round(v, 6);
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