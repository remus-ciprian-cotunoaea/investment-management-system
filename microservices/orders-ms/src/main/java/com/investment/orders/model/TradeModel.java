package com.investment.orders.model;

import com.investment.orders.utils.enums.TradeStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TradeModel {

    private final UUID id;                // trade_id (puede ser null antes de persistir)
    private final UUID instrumentId;
    private final UUID orderId;
    private final UUID accountId;

    private final BigDecimal quantity;    // numeric(28,10)
    private final BigDecimal price;       // numeric(18,6)
    private final BigDecimal fees;        // numeric(18,6)
    private final BigDecimal taxes;       // numeric(18,6)

    private final OffsetDateTime executedAt; // timestamptz
    private final LocalDate settlementDate;   // date (opcional)

    private final TradeStatusEnum status; // EXECUTED, CANCELED, CORRECTED, FAILED, SETTLED, PARTIALLY_SETTLED
}