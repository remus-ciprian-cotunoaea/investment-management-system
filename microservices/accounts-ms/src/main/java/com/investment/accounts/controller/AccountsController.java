package com.investment.accounts.controller;

import com.investment.accounts.dto.AccountRequestDto;
import com.investment.accounts.dto.AccountResponseDto;
import com.investment.accounts.dto.CashMovementResponseDto;
import com.investment.accounts.model.AccountModel;
import com.investment.accounts.model.CashMovementModel;
import com.investment.accounts.service.AccountService;
import com.investment.accounts.service.AccountsEventProducer;
import com.investment.accounts.service.SettlementService;
import com.investment.accounts.utils.DateTimeUtils;
import com.investment.accounts.utils.MoneyUtils;
import com.investment.accounts.utils.NumberUtils;
import com.investment.accounts.utils.enums.CashMovementStatusEnum;
import com.investment.accounts.utils.enums.CashMovementTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService accountService;
    private final AccountsEventProducer accountsEventProducer;
    private final SettlementService settlementService;

    // ===== CRUD principal =====

    @PostMapping
    @Transactional
    public ResponseEntity<AccountResponseDto> create(@RequestBody AccountRequestDto body) {
        AccountModel created = accountService.create(mapToModel(body, null));
        return ResponseEntity.ok(toDto(created));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<AccountResponseDto> update(@PathVariable UUID id, @RequestBody AccountRequestDto body) {
        AccountModel updated = accountService.update(mapToModel(body, id));
        return ResponseEntity.ok(toDto(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable UUID id) {
        return accountService.getById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<AccountResponseDto>> getAll(Pageable pageable) {
        Page<AccountResponseDto> page = accountService.getAll(pageable).map(this::toDto);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Ciclo de vida =====

    @PostMapping("/{id}/open")
    @Transactional
    public ResponseEntity<AccountResponseDto> open(@PathVariable UUID id) {
        return ResponseEntity.ok(toDto(accountService.open(id)));
    }

    @PostMapping("/{id}/close")
    @Transactional
    public ResponseEntity<AccountResponseDto> close(@PathVariable UUID id) {
        return ResponseEntity.ok(toDto(accountService.close(id)));
    }

    // ===== Eventos (Kafka) =====

    @PostMapping("/{id}/events/positions-recalculate")
    public ResponseEntity<Void> publishPositionsRecalculateRequested(@PathVariable UUID id) {
        accountsEventProducer.publishPositionsRecalculateRequested(id);
        return ResponseEntity.accepted().build();
    }

    // ===== Movimientos de efectivo & balance =====

    /**
     * Registrar movimiento de efectivo. Para no crear otro DTO,
     * se reciben parámetros simples.
     */
    @PostMapping("/{id}/movements")
    @Transactional
    public ResponseEntity<CashMovementResponseDto> registerMovement(
            @PathVariable UUID id,
            @RequestParam UUID currencyId,
            @RequestParam BigDecimal amount,
            @RequestParam CashMovementTypeEnum type,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) OffsetDateTime date // si no llega, usamos ahora()
    ) {
        // usa NumberUtils para “limpiar” nota vacía -> null
        String cleanNote = NumberUtils.trimOrNull(note);

        // normaliza/escala monto con MoneyUtils (p. ej. scale(2) o lógica que tengas)
        BigDecimal cleanAmount = MoneyUtils.normalize(amount);

        OffsetDateTime when = (date == null) ? DateTimeUtils.now() : date;

        CashMovementModel model = new CashMovementModel(
                null,              // id (lo genera DB)
                id,                // accountId
                currencyId,
                null,              // currencyCode (lo completa el service a partir del repo)
                cleanAmount,
                type,
                CashMovementStatusEnum.PENDING, // estado inicial
                when,
                cleanNote
        );

        CashMovementModel saved = settlementService.register(model);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping("/{id}/movements")
    public ResponseEntity<Page<CashMovementResponseDto>> getMovements(
            @PathVariable UUID id, Pageable pageable) {
        Page<CashMovementResponseDto> page =
                settlementService.getMovements(id, pageable).map(this::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(settlementService.getBalance(id));
    }

    // ====== mappers ======
    private AccountModel mapToModel(AccountRequestDto dto, UUID idOrNull) {
        // Usa NumberUtils para limpiar el accountNumber
        String accountNumber = NumberUtils.trimOrNull(dto.accountNumber());
        return new AccountModel(
                idOrNull,
                dto.brokerId(),
                null, // brokerName (lo rellena service via repo si hace falta para DTO)
                dto.currencyId(),
                null, // currencyCode (igual que brokerName)
                dto.portfolioId(),
                accountNumber,
                dto.type(),
                null, // status -> lo decide el service/negocio
                null, // openedAt
                null  // closedAt
        );
    }

    private AccountResponseDto toDto(AccountModel m) {
        return new AccountResponseDto(
                m.id(),
                m.brokerId(),
                m.brokerName(),
                m.currencyId(),
                m.currencyCode(),
                m.portfolioId(),
                m.accountNumber(),
                m.type(),
                m.status(),
                m.openedAt(),
                m.closedAt()
        );
    }

    private CashMovementResponseDto toDto(CashMovementModel m) {
        return new CashMovementResponseDto(
                m.id(),
                m.accountId(),
                m.currencyId(),
                m.currencyCode(),
                m.amount(),
                m.type(),
                m.status(),
                m.date(),
                m.note()
        );
    }
}