package com.investment.accounts.service;

import com.investment.accounts.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PricingService {

    // Instruments
    Optional<InstrumentEntity> getInstrumentById(UUID instrumentId);
    Page<InstrumentEntity> getInstrumentBySymbol(String symbol, Pageable pageable);
    Page<InstrumentEntity> getInstruments(Pageable pageable);

    // Prices (último y serie paginada)
    Optional<PriceEntity> getLastPrice(UUID instrumentId);
    Page<PriceEntity> getPrices(UUID instrumentId, Pageable pageable);

    // Exchanges
    Page<ExchangeEntity> getExchanges(Pageable pageable);

    // Exchange Listings (incluye la UNIQUE exchange_id + local_ticker)
    Optional<ExchangeListingEntity> getListingByExchangeAndLocalTicker(UUID exchangeId, String localTicker);
    Page<ExchangeListingEntity> getListingsByInstrument(UUID instrumentId, Pageable pageable);

    // Brokers (catálogo)
    Page<BrokerEntity> getBrokers(Pageable pageable);
}