package com.investment.orders.entity;

import com.investment.orders.utils.Constants;
import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity that represents an order stored in the orders schema/table.
 *
 * <p>This entity maps domain order data to the persistent representation used by
 * the orders' microservice. It contains identifiers for the order, instrument and
 * account, order side and type, requested quantity and optional limit price,
 * the order status and the time the order was placed. Numeric precision and
 * scale are defined on the {@code @Column} annotations to preserve financial accuracy.</p>
 *
 * <p>Typical usage:
 * - Persisted and retrieved via a Spring Data repository,
 * - Converted to/from DTOs by service or mapping layers,
 * - Used by business logic to manage order lifecycle.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Entity
@Table(name = Constants.ORDERS_GROUP, schema = Constants.ORDERS_GROUP)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @Column(name = Constants.ORDER_ID, nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = Constants.INSTRUMENT_ID, nullable = false)
    private UUID instrumentId;

    @Column(name = Constants.ACCOUNT_ID, nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.SIDE, nullable = false)
    private SideEnum side;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.ORDER_TYPE, nullable = false)
    private OrderTypeEnum orderType;

    @Column(name = Constants.QUANTITY, precision = Constants.INT_TWENTY_EIGHT, scale = Constants.INT_TEN, nullable = false)
    private BigDecimal quantity;

    @Column(name = Constants.LIMIT_PRICE, precision = Constants.INT_EIGHTEEN, scale = Constants.INT_SIX)
    private BigDecimal limitPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.STATUS, nullable = false)
    private OrderStatusEnum status;

    @Column(name = Constants.PLACED_AT, nullable = false)
    private Instant placedAt;

    @Column(name = Constants.NOTE)
    private String note;
}