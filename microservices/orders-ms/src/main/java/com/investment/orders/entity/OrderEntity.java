package com.investment.orders.entity;

import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "instrument_id", nullable = false)
    private UUID instrumentId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private SideEnum side;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderTypeEnum orderType;

    @Column(name = "quantity", precision = 28, scale = 10, nullable = false)
    private BigDecimal quantity;

    @Column(name = "limit_price", precision = 18, scale = 6)
    private BigDecimal limitPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEnum status;

    @Column(name = "placed_at", nullable = false)
    private Instant placedAt;

    @Column(name = "note")
    private String note;
}