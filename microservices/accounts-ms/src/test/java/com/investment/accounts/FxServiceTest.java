package com.investment.accounts;

import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.entity.FxRateEntity;
import com.investment.accounts.repository.CurrencyRepository;
import com.investment.accounts.repository.FxRateRepository;
import com.investment.accounts.service.impl.FxServiceImpl;
import com.investment.accounts.utils.MoneyUtils;
import com.investment.accounts.utils.FxUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FxServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private FxRateRepository fxRateRepository;

    @InjectMocks
    private FxServiceImpl service;

    private UUID usdId;
    private UUID eurId;

    @BeforeEach
    void init() {
        usdId = UUID.randomUUID();
        eurId = UUID.randomUUID();
    }

    // ===== Currencies =====

    @Test
    void getCurrencyByCode_shouldReturnMatchIgnoringCase() {
        // given
        CurrencyEntity usd = currency(usdId, "USD");
        CurrencyEntity eur = currency(eurId, "EUR");
        when(currencyRepository.findAll()).thenReturn(List.of(usd, eur));

        // when
        Optional<CurrencyEntity> found = service.getCurrencyByCode("usd");

        // then
        assertTrue(found.isPresent());
        assertEquals("USD", found.get().getCode());
        verify(currencyRepository).findAll();
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    void getCurrencyByCode_whenNoMatch_returnsEmpty() {
        when(currencyRepository.findAll()).thenReturn(List.of(currency(usdId, "USD")));
        Optional<CurrencyEntity> notFound = service.getCurrencyByCode("JPY");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void getCurrencies_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CurrencyEntity> page = new PageImpl<>(List.of(currency(usdId, "USD"), currency(eurId, "EUR")));
        when(currencyRepository.findAll(pageable)).thenReturn(page);

        Page<CurrencyEntity> result = service.getCurrencies(pageable);

        assertEquals(2, result.getNumberOfElements());
        verify(currencyRepository).findAll(pageable);
    }

    // ===== FX Rates =====

    @Test
    void getRates_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 5);
        FxRateEntity r1 = rate(usdId, eurId, new BigDecimal("0.90"));
        FxRateEntity r2 = rate(usdId, eurId, new BigDecimal("0.91"));
        when(fxRateRepository.findAllByFromCurrencyIdAndToCurrencyId(usdId, eurId, pageable))
                .thenReturn(new PageImpl<>(List.of(r1, r2)));

        Page<FxRateEntity> result = service.getRates(usdId, eurId, pageable);

        assertEquals(2, result.getNumberOfElements());
        verify(fxRateRepository).findAllByFromCurrencyIdAndToCurrencyId(usdId, eurId, pageable);
    }

    @Test
    void getLastRate_shouldReturnOptional() {
        FxRateEntity last = rate(usdId, eurId, new BigDecimal("0.95"));
        when(fxRateRepository.findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(usdId, eurId))
                .thenReturn(Optional.of(last));

        Optional<FxRateEntity> result = service.getLastRate(usdId, eurId);

        assertTrue(result.isPresent());
        assertBigDecimalEquals(new BigDecimal("0.95"), result.get().getRate());
        verify(fxRateRepository).findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(usdId, eurId);
    }

    // ===== Convert =====

    @Test
    void convert_shouldNormalizeAndApplyRate() {
        BigDecimal amount = new BigDecimal("100.00");
        FxRateEntity last = rate(usdId, eurId, new BigDecimal("0.87"));

        when(fxRateRepository.findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(usdId, eurId))
                .thenReturn(Optional.of(last));

        BigDecimal result = service.convert(amount, usdId, eurId);

        BigDecimal expected = FxUtils.applyRate(MoneyUtils.normalize(amount), last.getRate());
        assertBigDecimalEquals(expected, result);
        verify(fxRateRepository).findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(usdId, eurId);
    }

    @Test
    void convert_whenNoRate_throwsIllegalState() {
        when(fxRateRepository.findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(usdId, eurId))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.convert(new BigDecimal("50"), usdId, eurId));

        assertTrue(ex.getMessage().toLowerCase().contains("fx rate"));
        verify(fxRateRepository).findTopByFromCurrencyIdAndToCurrencyIdOrderByIdTsDesc(usdId, eurId);
    }

    // ===== helpers =====

    private static CurrencyEntity currency(UUID id, String code) {
        CurrencyEntity c = new CurrencyEntity();
        c.setId(id);
        c.setCode(code);
        return c;
    }

    private static FxRateEntity rate(UUID fromId, UUID toId, BigDecimal r) {
        FxRateEntity e = new FxRateEntity();
        FxRateEntity.FxRateId id = new FxRateEntity.FxRateId();
        id.setFromCurrencyId(fromId);
        id.setToCurrencyId(toId);
        id.setTs(java.time.OffsetDateTime.now());
        e.setId(id);
        e.setRate(r);
        return e;
    }

    private static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertNotNull(actual, "actual BigDecimal is null");
        assertEquals(0, expected.compareTo(actual),
                () -> "Expected " + expected + " but was " + actual);
    }
}