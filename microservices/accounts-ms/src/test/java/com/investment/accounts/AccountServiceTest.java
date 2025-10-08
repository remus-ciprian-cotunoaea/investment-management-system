package com.investment.accounts;

import com.investment.accounts.entity.AccountEntity;
import com.investment.accounts.entity.BrokerEntity;
import com.investment.accounts.entity.CurrencyEntity;
import com.investment.accounts.model.AccountModel;
import com.investment.accounts.repository.AccountRepository;
import com.investment.accounts.repository.BrokerRepository;
import com.investment.accounts.repository.CurrencyRepository;
import com.investment.accounts.service.impl.AccountServiceImpl;
import com.investment.accounts.utils.enums.AccountStatusEnum;
import com.investment.accounts.utils.enums.AccountTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private BrokerRepository brokerRepository;
    @Mock private CurrencyRepository currencyRepository;

    @InjectMocks
    private AccountServiceImpl service;

    // ===== helpers =====
    private BrokerEntity broker(UUID id) {
        BrokerEntity b = new BrokerEntity();
        b.setId(id);
        b.setName("Broker X");
        return b;
    }

    private CurrencyEntity currency(UUID id) {
        CurrencyEntity c = new CurrencyEntity();
        c.setId(id);
        c.setCode("USD");
        return c;
    }

    private AccountEntity persistedAccount(UUID id, UUID brokerId, UUID currencyId) {
        AccountEntity e = new AccountEntity();
        e.setId(id);
        e.setBroker(broker(brokerId));
        e.setCurrency(currency(currencyId));
        e.setPortfolioId(UUID.randomUUID());
        e.setAccountNumber("ACC-001");
        e.setType(AccountTypeEnum.CASH);
        e.setStatus(AccountStatusEnum.ACTIVE);
        return e;
    }

    @Test
    void create_shouldMapAndPersist_andReturnModel() {
        // arrange
        UUID brokerId   = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();
        UUID portfolio  = UUID.randomUUID();

        AccountModel input = new AccountModel(
                null,                 // id (será asignado por "save")
                brokerId,             // brokerId
                null,                 // brokerName
                currencyId,           // currencyId
                null,                 // currencyCode
                portfolio,
                "ACC-001",
                AccountTypeEnum.CASH,
                AccountStatusEnum.ACTIVE,
                null,
                null
        );

        // resolver broker/currency en el servicio
        when(brokerRepository.findById(brokerId))
                .thenReturn(Optional.of(broker(brokerId)));
        when(currencyRepository.findById(currencyId))
                .thenReturn(Optional.of(currency(currencyId)));

        // IMPORTANTÍSIMO: que save devuelva un entity válido (con id)
        when(accountRepository.save(any(AccountEntity.class)))
                .thenAnswer(inv -> {
                    AccountEntity ent = inv.getArgument(0);
                    if (ent.getId() == null) {
                        ent.setId(UUID.randomUUID());
                    }
                    return ent;
                });


        ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);

        // act
        AccountModel result = service.create(input);

        // assert (persistencia)
        verify(accountRepository).save(captor.capture());
        AccountEntity saved = captor.getValue();

        assertNotNull(saved);
        assertEquals("ACC-001", saved.getAccountNumber());
        assertEquals(AccountTypeEnum.CASH, saved.getType());
        assertEquals(AccountStatusEnum.ACTIVE, saved.getStatus());
        assertNotNull(saved.getOpenedAt(), "openedAt se setea si status=ACTIVE");

        assertNotNull(saved.getBroker());
        assertEquals(brokerId, saved.getBroker().getId());
        assertEquals("Broker X", saved.getBroker().getName());

        assertNotNull(saved.getCurrency());
        assertEquals(currencyId, saved.getCurrency().getId());
        assertEquals("USD", saved.getCurrency().getCode());

        // assert (modelo de salida)
        assertNotNull(result, "service.create debe retornar un modelo, no null");
        assertEquals("ACC-001", result.accountNumber());
        assertEquals(brokerId, result.brokerId());
        assertEquals(currencyId, result.currencyId());
        assertEquals(AccountTypeEnum.CASH, result.type());
        assertEquals(AccountStatusEnum.ACTIVE, result.status());
    }

    @Test
    void update_shouldValidateAndPersist() {
        UUID id         = UUID.randomUUID();
        UUID brokerId   = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();

        AccountEntity existing = persistedAccount(id, brokerId, currencyId);
        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));

        AccountModel input = new AccountModel(
                id,
                brokerId,
                null,
                currencyId,
                null,
                existing.getPortfolioId(),
                "ACC-002",
                AccountTypeEnum.MARGIN,
                AccountStatusEnum.ACTIVE,
                existing.getOpenedAt(),
                null
        );

        when(brokerRepository.findById(brokerId)).thenReturn(Optional.of(existing.getBroker()));
        when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(existing.getCurrency()));
        when(accountRepository.save(any(AccountEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AccountModel result = service.update(input);

        assertNotNull(result);
        assertEquals("ACC-002", result.accountNumber());
        assertEquals(AccountTypeEnum.MARGIN, result.type());
        assertEquals(AccountStatusEnum.ACTIVE, result.status());
    }

    @Test
    void open_shouldSetActive() {
        UUID id         = UUID.randomUUID();
        UUID brokerId   = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();

        AccountEntity existing = persistedAccount(id, brokerId, currencyId);
        existing.setStatus(AccountStatusEnum.CLOSED);

        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(AccountEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AccountModel result = service.open(id);

        assertNotNull(result);
        assertEquals(AccountStatusEnum.ACTIVE, result.status());
        assertNotNull(result.openedAt());
        assertNull(result.closedAt());
    }

    @Test
    void close_shouldSetClosed() {
        UUID id         = UUID.randomUUID();
        UUID brokerId   = UUID.randomUUID();
        UUID currencyId = UUID.randomUUID();

        AccountEntity existing = persistedAccount(id, brokerId, currencyId);
        existing.setStatus(AccountStatusEnum.ACTIVE);

        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(AccountEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AccountModel result = service.close(id);

        assertNotNull(result);
        assertEquals(AccountStatusEnum.CLOSED, result.status());
        assertNotNull(result.closedAt());
    }
}