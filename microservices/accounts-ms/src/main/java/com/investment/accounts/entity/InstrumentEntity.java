package com.investment.accounts.entity;

import com.investment.accounts.utils.enums.InstrumentStatusEnum;
import com.investment.accounts.utils.enums.InstrumentTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "instruments",
        schema = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_instruments_symbol", columnNames = "symbol")
        }
)
public class InstrumentEntity {

    @Id
    @Column(name = "instrument_id", nullable = false)
    private UUID id;

    // U, NN -> único y not null
    @Column(name = "symbol", nullable = false, length = 40)
    private String symbol;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private InstrumentTypeEnum type;   // STOCK, BOND, FUTURE, OPTION, CRYPTO, FX, OTHER

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InstrumentStatusEnum status; // ACTIVE, INACTIVE, DELISTED, ARCHIVED

    // FK -> currencies.currency_id (NN)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currency;
}