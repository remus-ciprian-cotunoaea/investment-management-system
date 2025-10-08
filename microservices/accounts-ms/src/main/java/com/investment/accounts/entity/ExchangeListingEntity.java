package com.investment.accounts.entity;

import com.investment.accounts.utils.enums.ListingStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Tabla: accounts.exchange_listing
 * PK compuesta: instrument_id + exchange_id
 * Unique: exchange_id + local_ticker
 * FK: instrument_id → instruments, exchange_id → exchanges, trade_currency_id → currencies
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "exchange_listing",
        schema = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_exchange_listing_exchange_localticker",
                        columnNames = {"exchange_id", "local_ticker"}
                )
        }
)
public class ExchangeListingEntity {

    @EmbeddedId
    private ExchangeListingId id;

    // NN
    @Column(name = "local_ticker", nullable = false, length = 40)
    private String localTicker;

    // FK, NN
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_currency_id", nullable = false)
    private CurrencyEntity tradeCurrency;

    // NN
    @Column(name = "listing_date", nullable = false)
    private LocalDate listingDate;

    // NN enum
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ListingStatusEnum status;

    /**
     * ID embebido para PK compuesta (instrument_id + exchange_id)
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ExchangeListingId {

        @Column(name = "instrument_id", nullable = false)
        private UUID instrumentId;

        @Column(name = "exchange_id", nullable = false)
        private UUID exchangeId;
    }
}