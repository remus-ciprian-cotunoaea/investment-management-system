package com.investment.positions.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "positions",
        schema = "positions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_positions_account_instrument",
                        columnNames = {"account_id", "instrument_id"}
                )
        }
)
public class PositionEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "position_id", nullable = false, updatable = false)
    private UUID positionId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;  // FK hacia accounts_ms

    @Column(name = "instrument_id", nullable = false)
    private UUID instrumentId;  // FK hacia instruments_ms

    @Column(name = "quantity", nullable = false, precision = 28, scale = 10)
    private BigDecimal quantity;

    @Column(name = "avg_cost", precision = 18, scale = 6)
    private BigDecimal avgCost;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime lastUpdated;
}