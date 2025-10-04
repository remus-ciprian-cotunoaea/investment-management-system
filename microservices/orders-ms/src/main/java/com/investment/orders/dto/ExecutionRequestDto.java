package com.investment.orders.dto;

import com.investment.orders.utils.enums.TradeStatusEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecutionRequestDto {

    @NotNull
    private UUID instrumentId;

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID accountId;

    @NotNull @Positive
    @Digits(integer = 28, fraction = 10)
    private BigDecimal quantity;          // numeric(28,10)

    @NotNull @Positive
    @Digits(integer = 18, fraction = 6)
    private BigDecimal price;             // numeric(18,6)

    @PositiveOrZero
    @Digits(integer = 18, fraction = 6)
    private BigDecimal fees;              // numeric(18,6)

    @PositiveOrZero
    @Digits(integer = 18, fraction = 6)
    private BigDecimal taxes;             // numeric(18,6)

    @NotNull
    private OffsetDateTime executedAt;    // timestamptz

    private LocalDate settlementDate;     // date (opcional)

    private TradeStatusEnum status;       // EXECUTED, CANCELED, CORRECTED, FAILED, SETTLED, PARTIALLY_SETTLED
}