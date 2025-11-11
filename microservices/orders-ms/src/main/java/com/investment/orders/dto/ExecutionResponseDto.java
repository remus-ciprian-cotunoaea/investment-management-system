package com.investment.orders.dto;

import com.investment.orders.utils.enums.TradeStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

// filepath: d:\GITHUB\investment-management-system\microservices\orders-ms\src\main\java\com\investment\orders\dto\ExecutionResponseDto.java

/**
 * Response Data Transfer Object representing an executed trade for an order.
 *
 * <p>This DTO carries identifying information (ids), executed quantity and price,
 * monetary adjustments (fees, taxes), timestamps for execution and settlement, and
 * the trade status. It is used by service and controller layers to return execution
 * details to callers (for example REST API responses).</p>
 *
 * <p>Note: Numeric precision and validation are handled elsewhere (persistence or request DTOs);
 * this class is a simple immutable/POJO representation used for output.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
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