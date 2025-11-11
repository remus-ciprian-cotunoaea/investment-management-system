package com.investment.orders.model;

import com.investment.orders.utils.enums.TradeStatusEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable record that represents a trade (execution) within the orders' microservice.
 *
 * <p>This model carries core trade information used across service and mapping layers:
 * identifiers linking the trade to instrument, order and account; executed quantity and price;
 * monetary adjustments (fees, taxes); timestamps for execution and optional settlement date;
 * and a status enum describing the trade outcome.</p>
 *
 * <p>Intended as a lightweight, immutable data carrier between layers (service, repository mappers,
 * and API mapping). Precision, validation and persistence details are handled elsewhere.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Builder
public record TradeModel(
        UUID id,
        UUID instrumentId,
        UUID orderId,
        UUID accountId,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal fees,
        BigDecimal taxes,
        OffsetDateTime executedAt,
        LocalDate settlementDate,
        TradeStatusEnum status
) {}