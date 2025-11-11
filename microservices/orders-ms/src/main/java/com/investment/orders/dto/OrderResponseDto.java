package com.investment.orders.dto;

import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response Data Transfer Object representing an order returned by the service/API.
 *
 * <p>This DTO contains identifying information (IDs), order attributes (side, type),
 * quantity/limit price, status, timestamp when the order was placed, and an optional note.
 * It is used to present order data to callers (for example REST API responses).</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private UUID id;                      // order_id
    private UUID instrumentId;
    private UUID accountId;

    private SideEnum side;
    private OrderTypeEnum orderType;

    private BigDecimal quantity;
    private BigDecimal limitPrice;

    private OrderStatusEnum status;

    private OffsetDateTime placedAt;      // timestamp
    private String note;
}