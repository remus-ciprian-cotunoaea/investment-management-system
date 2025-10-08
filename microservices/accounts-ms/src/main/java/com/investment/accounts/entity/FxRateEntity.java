package com.investment.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fx_rates", schema = "accounts")
public class FxRateEntity {

    @EmbeddedId
    private FxRateId id;

    // FK → currencies (from)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fromCurrencyId")
    @JoinColumn(name = "from_currency_id", nullable = false)
    private CurrencyEntity fromCurrency;

    // FK → currencies (to)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("toCurrencyId")
    @JoinColumn(name = "to_currency_id", nullable = false)
    private CurrencyEntity toCurrency;

    // NN numeric(20,10)
    @Column(name = "rate", nullable = false, precision = 20, scale = 10)
    private BigDecimal rate;

    /**
     * PK compuesta: ts + from_currency_id + to_currency_id
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class FxRateId {

        // timestamptz
        @Column(name = "ts", nullable = false)
        private OffsetDateTime ts;

        @Column(name = "from_currency_id", nullable = false)
        private UUID fromCurrencyId;

        @Column(name = "to_currency_id", nullable = false)
        private UUID toCurrencyId;
    }
}