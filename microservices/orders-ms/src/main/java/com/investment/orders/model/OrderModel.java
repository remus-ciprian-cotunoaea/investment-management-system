package com.investment.orders.model;

import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class OrderModel {

    private final UUID id;                // order_id (puede ser null antes de persistir)
    private final UUID instrumentId;
    private final UUID accountId;

    private final SideEnum side;          // BUY / SELL
    private final OrderTypeEnum orderType;// MARKET / LIMIT / STOP / STOP_LIMIT / OTHER

    private final BigDecimal quantity;    // numeric(28,10)
    private final BigDecimal limitPrice;  // numeric(18,6) - opcional para MARKET

    private final OrderStatusEnum status; // PENDING, FILLED, PARTIALLY_FILLED, CANCELED, REJECTED, FAILED

    private final OffsetDateTime placedAt; // timestamptz
    private final String note;            // text (opcional)
}