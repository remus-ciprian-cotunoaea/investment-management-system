package com.investment.positions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Respuesta expuesta por la API. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PositionResponseDto(
        UUID positionId,
        UUID accountId,
        UUID instrumentId,
        BigDecimal quantity,
        BigDecimal avgCost,
        OffsetDateTime lastUpdated
) { }