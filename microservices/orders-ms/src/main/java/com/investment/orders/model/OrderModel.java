package com.investment.orders.model;

import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable record model that represents an order within the orders' microservice.
 *
 * <p>This record carries core order information used across service and mapping layers:
 * identifiers (order, instrument, account), order side and type, requested quantity and optional
 * limit price, current order status, the timestamp when the order was placed, and an optional note.</p>
 *
 * <p>Intended for internal use as a lightweight, immutable data carrier between layers
 * (service, repository mappers, and API mapping).</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Builder
public record OrderModel(
        UUID id,
        UUID instrumentId,
        UUID accountId,
        SideEnum side,
        OrderTypeEnum orderType,
        BigDecimal quantity,
        BigDecimal limitPrice,
        OrderStatusEnum status,
        OffsetDateTime placedAt,
        String note
) {}

