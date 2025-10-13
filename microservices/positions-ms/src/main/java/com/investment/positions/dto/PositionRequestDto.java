package com.investment.positions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

/** Petición para crear/actualizar una posición. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PositionRequestDto(
        @NotNull UUID accountId,
        @NotNull UUID instrumentId,
        @NotNull @PositiveOrZero BigDecimal quantity,
        BigDecimal avgCost
) { }