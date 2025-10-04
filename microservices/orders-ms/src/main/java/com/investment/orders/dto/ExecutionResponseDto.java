package com.investment.orders.dto;

import com.investment.orders.utils.enums.TradeStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionResponseDto {

    private UUID id;                      // trade_id
    private UUID instrumentId;
    private UUID orderId;
    private UUID accountId;

    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal fees;
    private BigDecimal taxes;

    private OffsetDateTime executedAt;
    private LocalDate settlementDate;

    private TradeStatusEnum status;
}