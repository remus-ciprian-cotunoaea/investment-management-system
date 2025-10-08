package com.investment.accounts;

import com.investment.accounts.entity.*;
import com.investment.accounts.repository.*;
import com.investment.accounts.service.PricingService;
import com.investment.accounts.service.impl.PricingServiceImpl;
import com.investment.accounts.utils.enums.ListingStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    private InstrumentRepository instrumentRepository;
    private PriceRepository priceRepository;
    private ExchangeRepository exchangeRepository;
    private ExchangeListingRepository exchangeListingRepository;
    private BrokerRepository brokerRepository;

    private PricingService service;

    @BeforeEach
    void setUp() {
        instrumentRepository = mock(InstrumentRepository.class);
        priceRepository = mock(PriceRepository.class);
        exchangeRepository = mock(ExchangeRepository.class);
        exchangeListingRepository = mock(ExchangeListingRepository.class);
        brokerRepository = mock(BrokerRepository.class);

        service = new PricingServiceImpl(
                instrumentRepository,
                priceRepository,
                exchangeRepository,
                exchangeListingRepository,
                brokerRepository
        );
    }

    // ===== Instruments =====

    @Test
    void getInstruments_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<InstrumentEntity> page = new PageImpl<>(List.of(instrument(UUID.randomUUID(), "AAPL")));
        when(instrumentRepository.findAll(pageable)).thenReturn(page);

        Page<InstrumentEntity> result = service.getInstruments(pageable);

        assertSame(page, result);
        verify(instrumentRepository).findAll(pageable);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    @Test
    void getInstrumentById_shouldDelegateToRepo() {
        UUID id = UUID.randomUUID();
        Optional<InstrumentEntity> expected = Optional.of(instrument(id, "MSFT"));
        when(instrumentRepository.findById(id)).thenReturn(expected);

        Optional<InstrumentEntity> result = service.getInstrumentById(id);

        assertSame(expected, result);
        verify(instrumentRepository).findById(id);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    @Test
    void getInstrumentBySymbol_shouldReturnPageWithSingleElement_whenExactMatch() {
        String symbol = "TSLA";
        Pageable pageable = PageRequest.of(0, 20);
        InstrumentEntity inst = instrument(UUID.randomUUID(), symbol);
        when(instrumentRepository.findBySymbol(symbol)).thenReturn(Optional.of(inst));

        Page<InstrumentEntity> result = service.getInstrumentBySymbol(symbol, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(symbol, result.getContent().getFirst().getSymbol());
        verify(instrumentRepository).findBySymbol(symbol);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    @Test
    void getInstrumentBySymbol_shouldReturnEmptyPage_whenNotFound() {
        String symbol = "NOPE";
        Pageable pageable = PageRequest.of(0, 10);
        when(instrumentRepository.findBySymbol(symbol)).thenReturn(Optional.empty());

        Page<InstrumentEntity> result = service.getInstrumentBySymbol(symbol, pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(instrumentRepository).findBySymbol(symbol);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    // ===== Prices =====

    @Test
    void getPrices_shouldReturnPage() {
        UUID instrumentId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5); // puedes agregar Sort si quieres orden específico
        Page<PriceEntity> page = new PageImpl<>(List.of(price(instrumentId)));

        when(priceRepository.findByIdInstrumentId(eq(instrumentId), eq(pageable)))
                .thenReturn(page);

        Page<PriceEntity> result = service.getPrices(instrumentId, pageable);

        assertSame(page, result);
        verify(priceRepository).findByIdInstrumentId(eq(instrumentId), eq(pageable));
        verifyNoMoreInteractions(instrumentRepository, priceRepository,
                exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    @Test
    void getLastPrice_shouldDelegateToRepo() {
        UUID instrumentId = UUID.randomUUID();
        Optional<PriceEntity> expected = Optional.of(price(instrumentId));

        when(priceRepository.findTopByIdInstrumentIdOrderByIdTsDesc(eq(instrumentId)))
                .thenReturn(expected);

        Optional<PriceEntity> result = service.getLastPrice(instrumentId);

        assertSame(expected, result);
        verify(priceRepository).findTopByIdInstrumentIdOrderByIdTsDesc(eq(instrumentId));
        verifyNoMoreInteractions(priceRepository);
    }

    // ===== Exchanges =====

    @Test
    void getExchanges_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ExchangeEntity> page = new PageImpl<>(List.of(exchange()));
        when(exchangeRepository.findAll(pageable)).thenReturn(page);

        Page<ExchangeEntity> result = service.getExchanges(pageable);

        assertSame(page, result);
        verify(exchangeRepository).findAll(pageable);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    @Test
    void getListingsByInstrument_shouldReturnFilteredPageViaRepoMethod() {
        UUID instrumentId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        // si tu repo expone findByIdInstrumentId(...):
        when(exchangeListingRepository.findByIdInstrumentId(instrumentId, pageable))
                .thenReturn(new PageImpl<>(List.of(listing(instrumentId))));

        Page<ExchangeListingEntity> result = service.getListingsByInstrument(instrumentId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(instrumentId,
                result.getContent().getFirst().getId().getInstrumentId());
        verify(exchangeListingRepository).findByIdInstrumentId(instrumentId, pageable);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    @Test
    void getListingByExchangeAndLocalTicker_shouldReturnOptional() {
        UUID exchangeId = UUID.randomUUID();
        String localTicker = "AAPL";
        Optional<ExchangeListingEntity> expected = Optional.of(listing(UUID.randomUUID()));
        when(exchangeListingRepository.findByIdExchangeIdAndLocalTicker(exchangeId, localTicker))
                .thenReturn(expected);

        Optional<ExchangeListingEntity> result =
                service.getListingByExchangeAndLocalTicker(exchangeId, localTicker);

        assertSame(expected, result);
        verify(exchangeListingRepository).findByIdExchangeIdAndLocalTicker(exchangeId, localTicker);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    // ===== Brokers =====

    @Test
    void getBrokers_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BrokerEntity> page = new PageImpl<>(List.of(broker()));
        when(brokerRepository.findAll(pageable)).thenReturn(page);

        Page<BrokerEntity> result = service.getBrokers(pageable);

        assertSame(page, result);
        verify(brokerRepository).findAll(pageable);
        verifyNoMoreInteractions(instrumentRepository, priceRepository, exchangeRepository, exchangeListingRepository, brokerRepository);
    }

    // ===== Helpers =====

    private static InstrumentEntity instrument(UUID id, String symbol) {
        InstrumentEntity e = new InstrumentEntity();
        e.setId(id);
        e.setSymbol(symbol);
        return e;
    }

    private static PriceEntity price(UUID instrumentId) {
        PriceEntity e = new PriceEntity();
        PriceEntity.PriceId pid = new PriceEntity.PriceId();
        pid.setInstrumentId(instrumentId);
        pid.setTs(OffsetDateTime.now());
        e.setId(pid);
        return e;
    }

    private static ExchangeEntity exchange() {
        ExchangeEntity e = new ExchangeEntity();
        e.setId(UUID.randomUUID());
        e.setName("NYSE");
        return e;
    }

    private static ExchangeListingEntity listing(UUID instrumentId) {
        ExchangeListingEntity e = new ExchangeListingEntity();
        // ID embebido correcto
        ExchangeListingEntity.ExchangeListingId id = new ExchangeListingEntity.ExchangeListingId();
        id.setInstrumentId(instrumentId);
        id.setExchangeId(UUID.randomUUID());
        e.setId(id);

        e.setListingDate(LocalDate.now());
        e.setStatus(ListingStatusEnum.LISTED);
        e.setLocalTicker("TCKR-" + instrumentId.toString().substring(0, 5));
        return e;
    }

    private static BrokerEntity broker() {
        BrokerEntity e = new BrokerEntity();
        e.setId(UUID.randomUUID());
        e.setName("Broker X");
        return e;
    }
}