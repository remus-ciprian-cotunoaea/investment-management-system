package com.investment.orders.service.impl;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.NotFoundException;
import com.investment.orders.dto.ExecutionRequestDto;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.entity.OrderEntity;
import com.investment.orders.entity.TradeEntity;
import com.investment.orders.service.KafkaProducer;
import com.investment.orders.model.OrderModel;
import com.investment.orders.model.TradeModel;
import com.investment.orders.utils.Constants;
import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.TradeStatusEnum;
import com.investment.orders.repository.OrderRepository;
import com.investment.orders.repository.TradeRepository;
import com.investment.orders.service.ExecutionService;
import com.investment.orders.utils.DateTimeUtils;
import com.investment.orders.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {

    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;

    // =========================
    // Commands
    // =========================
    @Override
    public ExecutionResponseDto execute(ExecutionRequestDto req) {
        if (!NumberUtils.isPositive(req.getQuantity()))
            throw new BadRequestException(Constants.QUANTITY_GREATER_THAN_ZERO);
        if (!NumberUtils.isPositive(req.getPrice()))
            throw new BadRequestException(Constants.PRICE_GREATER_THAN_ZERO);

        OrderEntity orderEntity = orderRepository
                .findByOrderIdAndAccountId(req.getOrderId(), req.getAccountId())
                .orElseThrow(() -> new NotFoundException(Constants.ORDER_NOT_FOUND_ACCOUNT));

        OrderModel order = toModel(orderEntity);

        BigDecimal qty   = NumberUtils.round(req.getQuantity(), Constants.INT_TEN);
        BigDecimal price = NumberUtils.round(req.getPrice(), Constants.INT_SIX);
        BigDecimal fees  = NumberUtils.round(nvl(req.getFees()), Constants.INT_SIX);
        BigDecimal taxes = NumberUtils.round(nvl(req.getTaxes()), Constants.INT_SIX);

        var executedAt = DateTimeUtils.toOffsetDateTime(Instant.now());

        TradeModel trade = TradeModel.builder()
                .id(UUID.randomUUID())
                .instrumentId(order.instrumentId())
                .orderId(order.id())
                .accountId(order.accountId())
                .quantity(qty)
                .price(price)
                .fees(fees)
                .taxes(taxes)
                .executedAt(executedAt)
                .settlementDate(req.getSettlementDate())
                .status(TradeStatusEnum.EXECUTED)
                .build();

        TradeEntity saved = tradeRepository.save(toEntity(trade));

        if (qty != null && order.quantity() != null && qty.compareTo(order.quantity()) >= Constants.INT_ZERO) {
            orderEntity.setStatus(OrderStatusEnum.FILLED);
        } else {
            orderEntity.setStatus(OrderStatusEnum.PARTIALLY_FILLED);
        }
        orderRepository.save(orderEntity);

        ExecutionResponseDto dto = toResponse(toModel(saved));

        kafkaProducer.publishTradeExecuted(dto);

        return dto;
    }

    // =========================
    // Queries
    // =========================
    @Override
    public Page<ExecutionResponseDto> findAllByOrder(UUID orderId, int page, int size) {
        if (!NumberUtils.isPositive(BigDecimal.valueOf(size))) // size <= 0
            throw new BadRequestException(Constants.SIZE_GREATER_THAN_ZERO);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Constants.EXECUTED).descending());
        return tradeRepository.findAllByOrderId(orderId, pageable)
                .map(this::toModel)
                .map(this::toResponse);
    }

    @Override
    public Page<ExecutionResponseDto> findAllByAccount(UUID accountId, int page, int size) {
        if (!NumberUtils.isPositive(BigDecimal.valueOf(size))) // size <= 0
            throw new BadRequestException(Constants.SIZE_GREATER_THAN_ZERO);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Constants.EXECUTED).descending());
        return tradeRepository.findAllByAccountId(accountId, pageable)
                .map(this::toModel)
                .map(this::toResponse);
    }

    @Override
    public ExecutionResponseDto getLastExecutionOfOrder(UUID orderId) {
        TradeEntity last = tradeRepository.findTopByOrderIdOrderByExecutedAtDesc(orderId)
                .orElseThrow(() -> new NotFoundException(Constants.NO_EXECUTION_FOR_ORDER));
        return toResponse(toModel(last));
    }

    // =========================
    // Mappers: Entity ↔ Model
    // =========================
    private OrderModel toModel(OrderEntity e) {
        return OrderModel.builder()
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

    private TradeModel toModel(TradeEntity e) {
        var executed = e.getExecutedAt() == null
                ? null
                : DateTimeUtils.toOffsetDateTime(DateTimeUtils.toInstant(e.getExecutedAt()));

        return TradeModel.builder()
                .id(e.getTradeId())
                .instrumentId(e.getInstrumentId())
                .orderId(e.getOrderId())
                .accountId(e.getAccountId())
                .quantity(NumberUtils.round(e.getQuantity(), Constants.INT_TEN))
                .price(NumberUtils.round(e.getPrice(), Constants.INT_SIX))
                .fees(NumberUtils.round(e.getFees(), Constants.INT_SIX))
                .taxes(NumberUtils.round(e.getTaxes(), Constants.INT_SIX))
                .executedAt(executed)
                .settlementDate(e.getSettlementDate())
                .status(e.getStatus())
                .build();
    }

    private TradeEntity toEntity(TradeModel m) {
        return TradeEntity.builder()
                .tradeId(m.id())
                .instrumentId(m.instrumentId())
                .orderId(m.orderId())
                .accountId(m.accountId())
                .quantity(m.quantity())
                .price(m.price())
                .fees(m.fees())
                .taxes(m.taxes())
                .executedAt(m.executedAt())
                .settlementDate(m.settlementDate())
                .status(m.status())
                .build();
    }

    // =========================
    // DTO mapper
    // =========================
    private ExecutionResponseDto toResponse(TradeModel m) {
        return ExecutionResponseDto.builder()
                .id(m.id())
                .instrumentId(m.instrumentId())
                .orderId(m.orderId())
                .accountId(m.accountId())
                .quantity(NumberUtils.round(m.quantity(), Constants.INT_TEN))
                .price(NumberUtils.round(m.price(), Constants.INT_SIX))
                .fees(NumberUtils.round(m.fees(), Constants.INT_SIX))
                .taxes(NumberUtils.round(m.taxes(), Constants.INT_SIX))
                .executedAt(m.executedAt())
                .settlementDate(m.settlementDate())
                .status(m.status())
                .build();
    }

    // =========================
    // Helpers
    // =========================
    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}