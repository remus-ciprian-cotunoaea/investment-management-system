package com.investment.positions.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investment.common.exception.BadRequestException;
import com.investment.positions.dto.PositionRequestDto;
import com.investment.positions.dto.PositionResponseDto;
import com.investment.positions.entity.PositionEntity;
import com.investment.positions.repository.PositionRepository;
import com.investment.positions.service.RecalculationService;
import com.investment.positions.util.DateTimeUtils;
import com.investment.positions.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecalculationServiceImpl implements RecalculationService {

    private static final int QTY_SCALE = 10; // quantity numeric(28,10)
    private static final int COST_SCALE = 6; // avg_cost numeric(18,6)

    private final PositionRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PositionResponseDto recalculate(PositionRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }

        BigDecimal qty = NumberUtils.scale(request.quantity(), QTY_SCALE);
        BigDecimal avgCost = NumberUtils.scale(request.avgCost(), COST_SCALE);

        if (NumberUtils.isNullOrZero(qty)) {
            avgCost = null;
        } else if (NumberUtils.isNullOrZero(avgCost)) {
            avgCost = null;
        }

        PositionEntity entity = repository
                .findByAccountIdAndInstrumentId(request.accountId(), request.instrumentId())
                .orElseGet(() -> PositionEntity.builder()
                        .accountId(request.accountId())
                        .instrumentId(request.instrumentId())
                        .build());

        entity.setQuantity(qty);
        entity.setAvgCost(avgCost);
        entity.setLastUpdated(DateTimeUtils.nowUtc()); // uso de DateTimeUtils

        PositionEntity saved = repository.save(entity);
        return new PositionResponseDto(
                saved.getPositionId(),
                saved.getAccountId(),
                saved.getInstrumentId(),
                saved.getQuantity(),
                saved.getAvgCost(),
                saved.getLastUpdated()
        );
    }

    @Override
    @Transactional
    public void processRecalculation(String message) {
        try {
            PositionRequestDto req = objectMapper.readValue(message, PositionRequestDto.class);
            recalculate(req);
        } catch (Exception e) {
            log.error("Failed to process recalculation message: {}", e.getMessage(), e);
        }
    }
}