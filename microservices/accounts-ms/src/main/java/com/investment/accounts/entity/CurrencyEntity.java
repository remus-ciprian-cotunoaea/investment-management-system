package com.investment.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Tabla: accounts.currencies
 * PK: currency_id
 * Unique: code
 * name: NOT NULL
 * symbol: opcional (según diagrama)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "currencies",
        schema = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_currency_code", columnNames = "code")
        }
)
public class CurrencyEntity {

    @Id
    @GeneratedValue
    @Column(name = "currency_id", nullable = false, updatable = false)
    private UUID id;

    // U, NN (único y no nulo)
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    // opcional (el diagrama no lo marca NN)
    @Column(name = "symbol", length = 8)
    private String symbol;

    // NN
    @Column(name = "name", nullable = false, length = 80)
    private String name;
}