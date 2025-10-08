package com.investment.accounts;

import com.investment.accounts.entity.AccountEntity;
import com.investment.accounts.entity.CashMovementEntity;
import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.model.CashMovementModel;
import com.investment.accounts.repository.AccountRepository;
import com.investment.accounts.repository.CashMovementRepository;
import com.investment.accounts.repository.CurrencyRepository;
import com.investment.accounts.service.impl.SettlementServiceImpl;
import com.investment.accounts.utils.MoneyUtils;
import com.investment.accounts.utils.enums.CashMovementStatusEnum;
import com.investment.accounts.utils.enums.CashMovementTypeEnum;
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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock private CashMovementRepository cashMovementRepository;
    @Mock private CurrencyRepository     currencyRepository;
    @Mock private AccountRepository      accountRepository;

    @InjectMocks
    private SettlementServiceImpl service;

    private UUID accountId;
    private UUID currencyId;
    private AccountEntity account;
    private CurrencyEntity currency;

    @BeforeEach
    void setUp() {
        accountId  = UUID.randomUUID();
        currencyId = UUID.randomUUID();

        account = new AccountEntity();
        account.setId(accountId);

        currency = new CurrencyEntity();
        currency.setId(currencyId);
        currency.setCode("USD");
    }

    @Test
    void register_shouldPersistPendingThenComplete_andReturnModel() {
        // Arrange
        CashMovementModel input = new CashMovementModel(
                null,
                accountId,
                currencyId,
                "USD",
                new BigDecimal("100.00"),
                CashMovementTypeEnum.DEPOSIT,
                null,
                null,
                "first deposit"
        );

        when(accountRepository.findById(eq(accountId))).thenReturn(Optional.of(account));
        when(currencyRepository.findById(eq(currencyId))).thenReturn(Optional.of(currency));

        List<CashMovementEntity> snapshots = new ArrayList<>();

        // Guardamos copias independientes del estado de cada save()
        when(cashMovementRepository.save(any(CashMovementEntity.class)))
                .thenAnswer(inv -> {
                    CashMovementEntity original = inv.getArgument(0);
                    CashMovementEntity copy = new CashMovementEntity();
                    copy.setAccountId(original.getAccountId());
                    copy.setCurrency(original.getCurrency());
                    copy.setAmount(original.getAmount());
                    copy.setType(original.getType());
                    copy.setStatus(original.getStatus());
                    copy.setDate(original.getDate());
                    copy.setNote(original.getNote());
                    snapshots.add(copy);
                    return original;
                });

        // Act
        CashMovementModel result = service.register(input);

        // Assert salida principal
        assertNotNull(result);
        assertEquals(CashMovementStatusEnum.COMPLETED, result.status());
        assertNotNull(result.date());

        // Verifica que hubo dos saves
        verify(cashMovementRepository, times(2)).save(any(CashMovementEntity.class));
        assertEquals(2, snapshots.size());

        // --- Primer save ---
        CashMovementEntity first = snapshots.getFirst();
        assertEquals(CashMovementStatusEnum.PENDING, first.getStatus());
        assertEquals(CashMovementTypeEnum.DEPOSIT, first.getType());
        assertEquals(MoneyUtils.normalize(new BigDecimal("100.00")), first.getAmount());
        assertEquals("first deposit", first.getNote());
        assertSame(account, first.getAccountId());
        assertSame(currency, first.getCurrency());

        // --- Segundo save ---
        CashMovementEntity second = snapshots.get(1);
        assertEquals(CashMovementStatusEnum.COMPLETED, second.getStatus());
        assertNotNull(second.getDate());
        assertEquals(CashMovementTypeEnum.DEPOSIT, second.getType());

        verify(accountRepository).findById(accountId);
        verify(currencyRepository).findById(currencyId);
        verifyNoMoreInteractions(accountRepository, currencyRepository, cashMovementRepository);
    }

    @Test
    void getMovements_shouldDelegateToRepository_andMap() {
        UUID accId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        CashMovementEntity e = new CashMovementEntity();
        e.setAccountId(account);
        e.setCurrency(currency);
        e.setType(CashMovementTypeEnum.WITHDRAWAL);
        e.setStatus(CashMovementStatusEnum.COMPLETED);
        e.setAmount(new BigDecimal("25.50"));
        e.setDate(OffsetDateTime.now());
        e.setNote("wd");

        Page<CashMovementEntity> page = new PageImpl<>(List.of(e));
        when(cashMovementRepository.findAllByAccountId(eq(accId), eq(pageable))).thenReturn(page);

        Page<CashMovementModel> result = service.getMovements(accId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        CashMovementModel m = result.getContent().getFirst();
        assertEquals(CashMovementTypeEnum.WITHDRAWAL, m.type());
        assertEquals(CashMovementStatusEnum.COMPLETED,  m.status());
        assertEquals(new BigDecimal("25.50"), m.amount());

        verify(cashMovementRepository).findAllByAccountId(eq(accId), eq(pageable));
        verifyNoMoreInteractions(cashMovementRepository);
    }

    @Test
    void getBalance_shouldReturnSumFromRepository() {
        UUID accId = UUID.randomUUID();
        BigDecimal sum = new BigDecimal("1234.56");

        when(cashMovementRepository.sumAmountByAccountId(eq(accId))).thenReturn(sum);

        BigDecimal result = service.getBalance(accId);

        assertEquals(sum, result);
        verify(cashMovementRepository).sumAmountByAccountId(eq(accId));
        verifyNoMoreInteractions(cashMovementRepository);
    }
}
