package com.investment.orders;

import com.investment.orders.dto.OrderRequestDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.entity.OrderEntity;
import com.investment.orders.repository.OrderRepository;
import com.investment.orders.service.impl.OrderServiceImpl;
import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderServiceImpl service;

    private UUID orderId;
    private UUID accountId;
    private UUID instrumentId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        instrumentId = UUID.randomUUID();
    }

    private OrderRequestDto baseReq() {
        return OrderRequestDto.builder()
                .instrumentId(instrumentId)
                .accountId(accountId)
                .side(SideEnum.BUY)
                .orderType(OrderTypeEnum.MARKET)
                .quantity(new BigDecimal("1.0000000000"))
                .limitPrice(null)
                .note("n")
                .build();
    }

    private OrderEntity entityFromReq(OrderRequestDto r, OrderStatusEnum status) {
        OrderEntity e = new OrderEntity();
        e.setOrderId(orderId);
        e.setInstrumentId(r.getInstrumentId());
        e.setAccountId(r.getAccountId());
        e.setSide(r.getSide());
        e.setOrderType(r.getOrderType());
        e.setQuantity(r.getQuantity());
        e.setLimitPrice(r.getLimitPrice());
        e.setStatus(status);
        e.setPlacedAt(Instant.now());
        e.setNote(r.getNote());
        return e;
    }

    // ---------- create ----------

    @Test
    @DisplayName("create() persiste y devuelve DTO")
    void create_ok() {
        OrderRequestDto req = baseReq();

        // repository.save devuelve entidad con id y placedAt poblados
        when(repository.save(any(OrderEntity.class)))
                .thenAnswer(inv -> {
                    OrderEntity toSave = inv.getArgument(0);
                    // simulamos que el repo llena id y placedAt
                    toSave.setOrderId(orderId);
                    toSave.setPlacedAt(Instant.now());
                    return toSave;
                });

        OrderResponseDto resp = service.create(req);

        assertThat(resp.getId()).isEqualTo(orderId);
        assertThat(resp.getAccountId()).isEqualTo(accountId);
        assertThat(resp.getInstrumentId()).isEqualTo(instrumentId);
        assertThat(resp.getStatus()).isEqualTo(OrderStatusEnum.PENDING);

        ArgumentCaptor<OrderEntity> cap = ArgumentCaptor.forClass(OrderEntity.class);
        verify(repository).save(cap.capture());
        assertThat(cap.getValue().getOrderType()).isEqualTo(OrderTypeEnum.MARKET);
        verifyNoMoreInteractions(repository);
    }

    // ---------- getByIdAndAccountId ----------

    @Test
    @DisplayName("getByIdAndAccountId() retorna DTO cuando existe")
    void getByIdAndAccountId_ok() {
        OrderRequestDto req = baseReq();
        OrderEntity stored = entityFromReq(req, OrderStatusEnum.PENDING);
        when(repository.findByOrderIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(stored));

        OrderResponseDto resp = service.getByIdAndAccountId(orderId, accountId);

        assertThat(resp.getId()).isEqualTo(orderId);
        assertThat(resp.getAccountId()).isEqualTo(accountId);
        verify(repository).findByOrderIdAndAccountId(orderId, accountId);
        verifyNoMoreInteractions(repository);
    }

    // ---------- update ----------

    @Test
    @DisplayName("update() permite actualizar solo si status=PENDING")
    void update_ok_whenPending() {
        OrderRequestDto update = baseReq().toBuilder()
                .orderType(OrderTypeEnum.LIMIT)
                .limitPrice(new BigDecimal("2.000001"))
                .build();

        OrderEntity stored = entityFromReq(baseReq(), OrderStatusEnum.PENDING);
        when(repository.findByOrderIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(stored));
        when(repository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDto resp = service.update(orderId, accountId, update);

        assertThat(resp.getOrderType()).isEqualTo(OrderTypeEnum.LIMIT);
        assertThat(resp.getLimitPrice()).isNotNull();
        verify(repository).findByOrderIdAndAccountId(orderId, accountId);
        verify(repository).save(any(OrderEntity.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("update() lanza excepción si status != PENDING")
    void update_notPending_throws() {
        OrderEntity stored = entityFromReq(baseReq(), OrderStatusEnum.FILLED);
        when(repository.findByOrderIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(stored));

        assertThrows(IllegalStateException.class,
                () -> service.update(orderId, accountId, baseReq()));

        verify(repository).findByOrderIdAndAccountId(orderId, accountId);
        verifyNoMoreInteractions(repository);
    }

    // ---------- delete ----------

    @Test
    @DisplayName("delete() elimina por id+account cuando existe")
    void delete_ok() {
        OrderEntity stored = entityFromReq(baseReq(), OrderStatusEnum.PENDING);
        when(repository.findByOrderIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(stored));

        service.delete(orderId, accountId);

        verify(repository).findByOrderIdAndAccountId(orderId, accountId);
        verify(repository).delete(stored);
        verifyNoMoreInteractions(repository);
    }

    // ---------- paginados ----------

    @Test
    @DisplayName("findAllByAccountId() mapea página")
    void findAllByAccountId_ok() {
        OrderEntity e = entityFromReq(baseReq(), OrderStatusEnum.PENDING);
        Page<OrderEntity> page = new PageImpl<>(List.of(e));
        when(repository.findAllByAccountId(eq(accountId), any(Pageable.class))).thenReturn(page);

        Page<OrderResponseDto> resp = service.findAllByAccountId(accountId, 0, 10);

        assertThat(resp.getTotalElements()).isEqualTo(1);
        assertThat(resp.getContent().getFirst().getId()).isEqualTo(orderId);
        verify(repository).findAllByAccountId(eq(accountId), any(Pageable.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("findAllByAccountIdAndStatus() mapea página")
    void findAllByAccountIdAndStatus_ok() {
        OrderEntity e = entityFromReq(baseReq(), OrderStatusEnum.PENDING);
        Page<OrderEntity> page = new PageImpl<>(List.of(e));
        when(repository.findAllByAccountIdAndStatus(eq(accountId), eq(OrderStatusEnum.PENDING), any(Pageable.class)))
                .thenReturn(page);

        Page<OrderResponseDto> resp =
                service.findAllByAccountIdAndStatus(accountId, OrderStatusEnum.PENDING, 0, 10);

        assertThat(resp.getTotalElements()).isEqualTo(1);
        verify(repository).findAllByAccountIdAndStatus(eq(accountId), eq(OrderStatusEnum.PENDING), any(Pageable.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("findAllByInstrumentId() mapea página")
    void findAllByInstrumentId_ok() {
        OrderEntity e = entityFromReq(baseReq(), OrderStatusEnum.PENDING);
        Page<OrderEntity> page = new PageImpl<>(List.of(e));
        when(repository.findAllByInstrumentId(eq(instrumentId), any(Pageable.class))).thenReturn(page);

        Page<OrderResponseDto> resp = service.findAllByInstrumentId(instrumentId, 0, 10);

        assertThat(resp.getTotalElements()).isEqualTo(1);
        verify(repository).findAllByInstrumentId(eq(instrumentId), any(Pageable.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("create() lanza IllegalArgumentException si quantity <= 0")
    void create_invalidQuantity_throws() {
        OrderRequestDto bad = baseReq().toBuilder()
                .quantity(new BigDecimal("-1"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> service.create(bad));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("create() LIMIT exige limitPrice no-negativo")
    void create_limit_requiresLimitPrice() {
        OrderRequestDto bad = baseReq().toBuilder()
                .orderType(OrderTypeEnum.LIMIT)
                .limitPrice(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> service.create(bad));
        verifyNoInteractions(repository);
    }
}