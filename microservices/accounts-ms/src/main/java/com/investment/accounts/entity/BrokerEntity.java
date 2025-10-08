package com.investment.accounts.entity;

import com.investment.accounts.utils.enums.BrokerStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Tabla: accounts.brokers
 * PK: broker_id
 * Unique: name
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "brokers",
        schema = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_broker_name", columnNames = "name")
        }
)
public class BrokerEntity {

    @Id
    @GeneratedValue
    @Column(name = "broker_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "country", nullable = false, length = 80)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private BrokerStatusEnum status; // ACTIVE, INACTIVE
}