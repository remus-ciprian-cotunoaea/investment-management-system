package com.investment.accounts.entity;

import com.investment.accounts.utils.enums.ExchangeStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Tabla: accounts.exchanges
 * PK: exchange_id
 * Unique: code
 * NN: code, name, country, timezone, status
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "exchanges",
        schema = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_exchange_code", columnNames = "code")
        }
)
public class ExchangeEntity {

    @Id
    @GeneratedValue
    @Column(name = "exchange_id", nullable = false, updatable = false)
    private UUID id;

    // U, NN
    @Column(name = "code", nullable = false, length = 16)
    private String code;

    // NN
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    // NN
    @Column(name = "country", nullable = false, length = 80)
    private String country;

    // NN
    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;

    // NN enum
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExchangeStatusEnum status;
}