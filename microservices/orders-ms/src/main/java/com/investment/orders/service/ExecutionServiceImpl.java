package com.investment.orders.service;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.NotFoundException;
import com.investment.orders.dto.ExecutionRequestDto;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.entity.OrderEntity;
import com.investment.orders.entity.TradeEntity;
import com.investment.orders.repository.OrderRepository;
import com.investment.orders.repository.TradeRepository;
import com.investment.orders.utils.DateTimeUtils;
import com.investment.orders.utils.NumberUtils;
import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.TradeStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {

    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer; // publica “trades.trade-executed”

    @Override
    public ExecutionResponseDto execute(ExecutionRequestDto req) {
        if (!NumberUtils.isPositive(req.getQuantity()))
            throw new BadRequestException("quantity must be > 0");
        if (!NumberUtils.isPositive(req.getPrice()))
            throw new BadRequestException("price must be > 0");

        // Buscar la orden con control de pertenencia a la cuenta
        OrderEntity order = orderRepository.findByOrderIdAndAccountId(req.getOrderId(), req.getAccountId())
                .orElseThrow(() -> new NotFoundException("order not found for account"));

        // Normalizar decimales (consistencia)
        var qty   = NumberUtils.round(req.getQuantity(), 10);
        var price = NumberUtils.round(req.getPrice(), 6);
        var fees  = NumberUtils.round(nvl(req.getFees()), 6);
        var taxes = NumberUtils.round(nvl(req.getTaxes()), 6);

        Instant now = Instant.now();

        TradeEntity trade = TradeEntity.builder()
                .tradeId(UUID.randomUUID())
                .instrumentId(order.getInstrumentId())
                .orderId(order.getOrderId())
                .accountId(order.getAccountId())
                .quantity(qty)
                .price(price)
                .fees(fees)
                .taxes(taxes)
                .executedAt(OffsetDateTime.from(now))
                .settlementDate(req.getSettlementDate())
                .status(TradeStatusEnum.EXECUTED)
                .build();

        var saved = tradeRepository.save(trade);

        // Actualizar estado de la orden (simplificado)
        // si qty == order.qty -> FILLED; si no, PARTIALLY_FILLED
        if (qty != null && order.getQuantity() != null && qty.compareTo(order.getQuantity()) >= 0) {
            order.setStatus(OrderStatusEnum.FILLED);
        } else {
            order.setStatus(OrderStatusEnum.PARTIALLY_FILLED);
        }
        orderRepository.save(order);

        var dto = toResponse(saved);

        // publicar evento
        kafkaProducer.publishTradeExecuted(dto);

        return dto;
    }

    @Override
    public Page<ExecutionResponseDto> findAllByOrder(UUID orderId, int page, int size) {
        if (size <= 0) throw new BadRequestException("size must be > 0");
        var pageable = PageRequest.of(page, size, Sort.by("executedAt").descending());
        return tradeRepository.findAllByOrderId(orderId, pageable).map(this::toResponse);
    }

    @Override
    public Page<ExecutionResponseDto> findAllByAccount(UUID accountId, int page, int size) {
        if (size <= 0) throw new BadRequestException("size must be > 0");
        var pageable = PageRequest.of(page, size, Sort.by("executedAt").descending());
        return tradeRepository.findAllByAccountId(accountId, pageable).map(this::toResponse);
    }

    @Override
    public ExecutionResponseDto getLastExecutionOfOrder(UUID orderId) {
        var last = tradeRepository.findTopByOrderIdOrderByExecutedAtDesc(orderId)
                .orElseThrow(() -> new NotFoundException("no executions for order"));
        return toResponse(last);
    }

    // =========================
    // Helpers
    // =========================
    private BigDecimal nvl(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private ExecutionResponseDto toResponse(TradeEntity e) {
        return ExecutionResponseDto.builder()
                .id(e.getTradeId())
                .instrumentId(e.getInstrumentId())
                .orderId(e.getOrderId())
                .accountId(e.getAccountId())
                .quantity(NumberUtils.round(e.getQuantity(), 10))
                .price(NumberUtils.round(e.getPrice(), 6))
                .fees(NumberUtils.round(e.getFees(), 6))
                .taxes(NumberUtils.round(e.getTaxes(), 6))
                .executedAt(DateTimeUtils.toOffsetDateTime(e.getExecutedAt().toInstant()))
                .settlementDate(e.getSettlementDate())
                .status(e.getStatus())
                .build();
    }
}