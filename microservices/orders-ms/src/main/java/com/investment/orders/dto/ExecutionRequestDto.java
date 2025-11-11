package com.investment.orders.dto;

import com.investment.orders.utils.Constants;
import com.investment.orders.utils.enums.TradeStatusEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a request to register an execution for an order.
 *
 * <p>This DTO carries the minimal information required to record an execution:
 * identifiers for instrument, order and account, the executed quantity and price,
 * optional fees/taxes, execution timestamp and optional settlement date and status.</p>
 *
 * <p>Validation constraints are applied on individual fields (e.g. {@code @NotNull},
 * {@code @Positive}, {@code @Digits}) to ensure payload integrity before processing.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionRequestDto {

    @NotNull
    private UUID instrumentId;

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID accountId;

    @NotNull
    @Positive
    @Digits(integer = Constants.INT_TWENTY_EIGHT, fraction = Constants.INT_TEN)
    private BigDecimal quantity;          // numeric(28,10)

    @NotNull
    @Positive
    @Digits(integer = Constants.INT_EIGHTEEN, fraction = Constants.INT_SIX)
    private BigDecimal price;             // numeric(18,6)

    @PositiveOrZero
    @Digits(integer = Constants.INT_EIGHTEEN, fraction = Constants.INT_SIX)
    private BigDecimal fees;              // numeric(18,6)

    @PositiveOrZero
    @Digits(integer = Constants.INT_EIGHTEEN, fraction = Constants.INT_SIX)
    private BigDecimal taxes;             // numeric(18,6)

    @NotNull
    private OffsetDateTime executedAt;    // timestamp

    private LocalDate settlementDate;     // date (optional)

    private TradeStatusEnum status;       // EXECUTED, CANCELED, CORRECTED, FAILED, SETTLED, PARTIALLY_SETTLED
}