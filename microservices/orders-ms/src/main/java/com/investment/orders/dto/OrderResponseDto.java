package com.investment.orders.dto;

import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

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

    private OffsetDateTime placedAt;      // timestamptz
    private String note;
}