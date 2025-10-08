package com.investment.accounts.service.impl;

import com.investment.accounts.entity.*;
import com.investment.accounts.repository.*;
import com.investment.accounts.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final InstrumentRepository instrumentRepository;
    private final PriceRepository priceRepository;
    private final ExchangeRepository exchangeRepository;
    private final ExchangeListingRepository exchangeListingRepository;
    private final BrokerRepository brokerRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<InstrumentEntity> getInstrumentById(UUID instrumentId) {
        return instrumentRepository.findById(instrumentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstrumentEntity> getInstrumentBySymbol(String symbol, Pageable pageable) {
        return instrumentRepository.findBySymbol(symbol)
                .map(e -> (Page<InstrumentEntity>) new org.springframework.data.domain.PageImpl<>(
                        java.util.List.of(e), pageable, 1))
                .orElseGet(() -> new org.springframework.data.domain.PageImpl<>(
                        java.util.List.of(), pageable, 0));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstrumentEntity> getInstruments(Pageable pageable) {
        return instrumentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceEntity> getLastPrice(UUID instrumentId) {
        return priceRepository.findTopByIdInstrumentIdOrderByIdTsDesc(instrumentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PriceEntity> getPrices(UUID instrumentId, Pageable pageable) {
        return priceRepository.findByIdInstrumentId(instrumentId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExchangeEntity> getExchanges(Pageable pageable) {
        return exchangeRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExchangeListingEntity> getListingByExchangeAndLocalTicker(UUID exchangeId, String localTicker) {
        return exchangeListingRepository.findByIdExchangeIdAndLocalTicker(exchangeId, localTicker);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExchangeListingEntity> getListingsByInstrument(UUID instrumentId, Pageable pageable) {
        return exchangeListingRepository.findByIdInstrumentId(instrumentId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrokerEntity> getBrokers(Pageable pageable) {
        return brokerRepository.findAll(pageable);
    }
}