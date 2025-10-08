package com.investment.accounts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "prices", schema = "accounts")
public class PriceEntity {

    @EmbeddedId
    private PriceId id;

    @MapsId("instrumentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private InstrumentEntity instrument;

    @Column(name = "open", nullable = false, precision = 18, scale = 6)
    private BigDecimal open;

    @Column(name = "high", nullable = false, precision = 18, scale = 6)
    private BigDecimal high;

    @Column(name = "low", nullable = false, precision = 18, scale = 6)
    private BigDecimal low;

    @Column(name = "close", nullable = false, precision = 18, scale = 6)
    private BigDecimal close;

    @Column(name = "volume", nullable = false, precision = 28, scale = 6)
    private BigDecimal volume;

    @Column(name = "source", nullable = false, length = 80)
    private String source;

    // ============================
    // Embedded ID Class
    // ============================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    @Embeddable
    public static class PriceId implements Serializable {

        @Column(name = "instrument_id", nullable = false)
        private UUID instrumentId;

        /** timestamptz */
        @Column(name = "ts", nullable = false)
        private OffsetDateTime ts;
    }
}