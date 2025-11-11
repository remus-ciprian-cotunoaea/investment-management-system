package com.investment.orders.dto;

import com.investment.orders.utils.Constants;
import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request Data Transfer Object used to create or update an order.
 *
 * <p>This DTO carries the minimal set of information required to place an order:
 * identifiers for the instrument and account, the order side and type, the requested
 * quantity, and an optional limit price. Validation constraints (Jakarta Validation)
 * are applied on the fields to ensure payload integrity before processing.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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
    @Digits(integer = Constants.INT_TWENTY_EIGHT, fraction = Constants.INT_TEN)
    private BigDecimal quantity;          // numeric(28,10)

    @Digits(integer = Constants.INT_EIGHTEEN, fraction = Constants.INT_SIX)
    @Positive
    private BigDecimal limitPrice;        // numeric(18,6) (optional)

    private OrderStatusEnum status;       // PENDING, FILLED, PARTIALLY_FILLED, CANCELED, REJECTED, FAILED

    private String note;                  // text (optional)
}