package com.investment.accounts.controller;

import com.investment.accounts.entity.*;
import com.investment.accounts.service.FxService;
import com.investment.accounts.service.PricingService;
import com.investment.accounts.utils.MoneyUtils;
import com.investment.accounts.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/refdata")
@RequiredArgsConstructor
public class RefDataController {

    private final FxService fxService;
    private final PricingService pricingService;

    // ===== Currencies & FX =====

    @GetMapping("/currencies")
    public ResponseEntity<?> getCurrencies(
            @RequestParam(required = false) String code,
            Pageable pageable
    ) {
        String clean = NumberUtils.trimOrNull(code);
        if (clean != null) {
            Optional<CurrencyEntity> one = fxService.getCurrencyByCode(clean);
            return one.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        Page<CurrencyEntity> page = fxService.getCurrencies(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/fx/last-rate")
    public ResponseEntity<?> getLastRate(
            @RequestParam UUID fromCurrencyId,
            @RequestParam UUID toCurrencyId
    ) {
        return fxService.getLastRate(fromCurrencyId, toCurrencyId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/fx/rates")
    public ResponseEntity<Page<FxRateEntity>> getRates(
            @RequestParam UUID fromCurrencyId,
            @RequestParam UUID toCurrencyId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(fxService.getRates(fromCurrencyId, toCurrencyId, pageable));
    }

    @GetMapping("/fx/convert")
    public ResponseEntity<BigDecimal> convert(
            @RequestParam UUID fromCurrencyId,
            @RequestParam UUID toCurrencyId,
            @RequestParam BigDecimal amount
    ) {
        // normaliza/scalado por MoneyUtils antes o después del cálculo
        BigDecimal normalized = MoneyUtils.normalize(amount);
        BigDecimal result = fxService.convert(normalized, fromCurrencyId, toCurrencyId);
        return ResponseEntity.ok(MoneyUtils.normalize(result));
    }

    // ===== Instruments & Prices =====

    @GetMapping("/instruments")
    public ResponseEntity<Page<InstrumentEntity>> getInstruments(
            @RequestParam(required = false) String symbol,
            Pageable pageable
    ) {
        String clean = NumberUtils.trimOrNull(symbol);
        if (clean != null) {
            return ResponseEntity.ok(pricingService.getInstrumentBySymbol(clean, pageable)); // <-- nombre y firma correctos
        }
        return ResponseEntity.ok(pricingService.getInstruments(pageable));
    }

    @GetMapping("/instruments/{id}")
    public ResponseEntity<Optional<InstrumentEntity>> getInstrumentById(@PathVariable UUID id) {
        return ResponseEntity.of(Optional.ofNullable(pricingService.getInstrumentById(id)));
    }

    @GetMapping("/prices")
    public ResponseEntity<Page<PriceEntity>> getPrices(
            @RequestParam UUID instrumentId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(pricingService.getPrices(instrumentId, pageable));
    }

    @GetMapping("/prices/last")
    public ResponseEntity<?> getLastPrice(@RequestParam UUID instrumentId) {
        return pricingService.getLastPrice(instrumentId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===== Exchanges, Listings, Brokers =====

    @GetMapping("/exchanges")
    public ResponseEntity<Page<ExchangeEntity>> getExchanges(Pageable pageable) {
        return ResponseEntity.ok(pricingService.getExchanges(pageable));
    }

    @GetMapping("/exchanges/{exchangeId}/listings/{localTicker}")
    public ResponseEntity<?> getListingByExchangeAndLocalTicker(
            @PathVariable UUID exchangeId,
            @PathVariable String localTicker
    ) {
        String ticker = NumberUtils.trimOrNull(localTicker);
        if (ticker == null) return ResponseEntity.badRequest().build();
        return pricingService.getListingByExchangeAndLocalTicker(exchangeId, ticker)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/instruments/{instrumentId}/listings")
    public ResponseEntity<Page<ExchangeListingEntity>> getListingsByInstrument(
            @PathVariable UUID instrumentId, Pageable pageable
    ) {
        return ResponseEntity.ok(pricingService.getListingsByInstrument(instrumentId, pageable));
    }

    @GetMapping("/brokers")
    public ResponseEntity<Page<BrokerEntity>> getBrokers(Pageable pageable) {
        return ResponseEntity.ok(pricingService.getBrokers(pageable));
    }
}