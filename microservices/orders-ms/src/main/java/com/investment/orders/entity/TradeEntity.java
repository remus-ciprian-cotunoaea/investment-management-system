package com.investment.orders.entity;

import com.investment.orders.utils.Constants;
import com.investment.orders.utils.enums.TradeStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA entity representing a trade (execution) record persisted by the orders' microservice.
 *
 * <p>This entity models an executed trade and contains identifiers linking the trade
 * to an instrument, an order and an account, as well as financial details (quantity,
 * price, fees, taxes), timestamps for execution and optional settlement date, and a
 * status enum describing the trade outcome.</p>
 *
 * <p>Column-level precision/scale and automatic timestamping are defined via the
 * {@code @Column} attributes and {@link CreationTimestamp}
 * respectively to ensure financial accuracy and reliable audit information.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Entity
@Table(name = Constants.TRADES_GROUP, schema = Constants.ORDERS_GROUP)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntity {

    @Id
    @Column(name = Constants.TRADE_ID, nullable = false, updatable = false)
    private UUID tradeId;

    @Column(name = Constants.INSTRUMENT_ID, nullable = false)
    private UUID instrumentId;

    @Column(name = Constants.ORDER_ID, nullable = false)
    private UUID orderId;

    @Column(name = Constants.ACCOUNT_ID, nullable = false)
    private UUID accountId;

    @Column(name = Constants.QUANTITY, nullable = false, precision = Constants.INT_TWENTY_EIGHT, scale = Constants.INT_TEN)
    private BigDecimal quantity;

    @Column(name = Constants.PRICE, nullable = false, precision = Constants.INT_EIGHTEEN, scale = Constants.INT_SIX)
    private BigDecimal price;

    @Column(name = Constants.FEES, precision = Constants.INT_EIGHTEEN, scale = Constants.INT_SIX)
    private BigDecimal fees;

    @Column(name = Constants.TAXES, precision = Constants.INT_EIGHTEEN, scale = Constants.INT_SIX)
    private BigDecimal taxes;

    @CreationTimestamp
    @Column(name = Constants.EXECUTED_AT, nullable = false, updatable = false)
    private OffsetDateTime executedAt;

    @Column(name = Constants.SETTLEMENT_DATE)
    private LocalDate settlementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.STATUS, nullable = false)
    private TradeStatusEnum status;
}