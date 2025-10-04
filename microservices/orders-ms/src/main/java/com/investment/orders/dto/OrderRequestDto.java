package com.investment.orders.dto;

import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    @NotNull
    private UUID instrumentId;

    @NotNull
    private UUID accountId;

    @NotNull
    private SideEnum side;                // BUY / SELL

    @NotNull
    private OrderTypeEnum orderType;      // MARKET / LIMIT / STOP / STOP_LIMIT / OTHER

    @NotNull @Positive
    @Digits(integer = 28, fraction = 10)
    private BigDecimal quantity;          // numeric(28,10)

    // Para LIMIT/STOP_LIMIT se usa; para MARKET puede ir null
    @Digits(integer = 18, fraction = 6)
    @Positive
    private BigDecimal limitPrice;        // numeric(18,6) (opcional)

    // El estado inicial típico será PENDING; lo dejamos opcional por si lo fijas desde el servicio
    private OrderStatusEnum status;       // PENDING, FILLED, PARTIALLY_FILLED, CANCELED, REJECTED, FAILED

    private String note;                  // text (opcional)
}