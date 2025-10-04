package com.investment.orders.entity;

import com.investment.orders.utils.enums.TradeStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "trades", schema = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntity {

    @Id
    @Column(name = "trade_id", nullable = false, updatable = false)
    private UUID tradeId;

    @Column(name = "instrument_id", nullable = false)
    private UUID instrumentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "quantity", nullable = false, precision = 28, scale = 10)
    private BigDecimal quantity;

    @Column(name = "price", nullable = false, precision = 18, scale = 6)
    private BigDecimal price;

    @Column(name = "fees", precision = 18, scale = 6)
    private BigDecimal fees;

    @Column(name = "taxes", precision = 18, scale = 6)
    private BigDecimal taxes;

    @CreationTimestamp
    @Column(name = "executed_at", nullable = false, updatable = false)
    private OffsetDateTime executedAt;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TradeStatusEnum status;
}