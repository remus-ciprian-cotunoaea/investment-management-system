package com.investment.orders;

import com.investment.orders.dto.OrderRequestDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.entity.OrderEntity;
import com.investment.orders.repository.OrderRepository;
import com.investment.orders.service.OrderServiceImpl;
import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.SideEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderServiceImpl service;

    private UUID orderId;
    private UUID accountId;
    private UUID instrumentId;
    private OrderEntity pendingOrder;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        orderId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        instrumentId = UUID.randomUUID();

        pendingOrder = new OrderEntity();
        pendingOrder.setOrderId(orderId);
        pendingOrder.setAccountId(accountId);
        pendingOrder.setInstrumentId(instrumentId);
        pendingOrder.setSide(SideEnum.BUY);
        pendingOrder.setOrderType(OrderTypeEnum.LIMIT);
        pendingOrder.setQuantity(new BigDecimal("10.0000000000"));
        pendingOrder.setLimitPrice(new BigDecimal("100.123456"));
        pendingOrder.setStatus(OrderStatusEnum.PENDING);
        pendingOrder.setPlacedAt(OffsetDateTime.now().toInstant());
        pendingOrder.setNote("init");
    }

    // ---- create ----
    @Test
    void create_shouldPersist_andReturnDto() {
        var req = OrderRequestDto.builder()
                .instrumentId(instrumentId)
                .accountId(accountId)
                .side(SideEnum.SELL)
                .orderType(OrderTypeEnum.MARKET)
                .quantity(new BigDecimal("5"))
                .limitPrice(null)
                .note("first")
                .build();

        when(repository.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity e = inv.getArgument(0);
            e.setOrderId(orderId);
            e.setStatus(OrderStatusEnum.PENDING);
            e.setPlacedAt(OffsetDateTime.now().toInstant());
            return e;
        });

        OrderResponseDto dto = service.create(req);

        // verify persisted values roughly match
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(repository).save(captor.capture());

        assertThat(dto.getId()).isEqualTo(orderId);
        assertThat(dto.getAccountId()).isEqualTo(accountId);
        assertThat(dto.getInstrumentId()).isEqualTo(instrumentId);
        assertThat(dto.getStatus()).isEqualTo(OrderStatusEnum.PENDING);
        assertThat(captor.getValue().getSide()).isEqualTo(SideEnum.SELL);
        assertThat(captor.getValue().getOrderType()).isEqualTo(OrderTypeEnum.MARKET);
    }

    // ---- getByIdAndAccountId ----
    @Test
    void getByIdAndAccountId_found_returnsDto() {
        when(repository.findByOrderIdAndAccountId(orderId, accountId))
                .thenReturn(Optional.of(pendingOrder));

        OrderResponseDto dto = service.getByIdAndAccountId(orderId, accountId);

        assertThat(dto.getId()).isEqualTo(orderId);
        verify(repository).findByOrderIdAndAccountId(orderId, accountId);
    }

    @Test
    void getByIdAndAccountId_notFound_throwsIllegalArgument() {
        when(repository.findByOrderIdAndAccountId(orderId, accountId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdAndAccountId(orderId, accountId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ---- paging: by account ----
    @Test
    void findAllByAccountId_returnsPage() {
        when(repository.findAllByAccountId(eq(accountId), any()))
                .thenAnswer(inv -> {
                    var p = (PageRequest) inv.getArgument(1);
                    return new PageImpl<>(List.of(pendingOrder), p, 1);
                });

        Page<OrderResponseDto> page = service.findAllByAccountId(accountId, 0, 20);

        assertThat(page.getContent()).hasSize(1);
        verify(repository).findAllByAccountId(eq(accountId), any());
    }

    @Test
    void findAllByAccountIdAndStatus_returnsPage() {
        when(repository.findAllByAccountIdAndStatus(eq(accountId), eq(OrderStatusEnum.PENDING), any()))
                .thenAnswer(inv -> {
                    var p = (PageRequest) inv.getArgument(2);
                    return new PageImpl<>(List.of(pendingOrder), p, 1);
                });

        Page<OrderResponseDto> page =
                service.findAllByAccountIdAndStatus(accountId, OrderStatusEnum.PENDING, 0, 10);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getStatus()).isEqualTo(OrderStatusEnum.PENDING);
    }

    // ---- by instrument ----
    @Test
    void findAllByInstrumentId_returnsPage() {
        when(repository.findAllByInstrumentId(eq(instrumentId), any()))
                .thenAnswer(inv -> new PageImpl<>(List.of(pendingOrder)));

        Page<OrderResponseDto> page = service.findAllByInstrumentId(instrumentId, 0, 5);

        assertThat(page.getTotalElements()).isEqualTo(1);
        verify(repository).findAllByInstrumentId(eq(instrumentId), any());
    }

    // ---- update ----
    @Test
    void update_whenPending_updatesAndReturnsDto() {
        when(repository.findByOrderIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(pendingOrder));
        when(repository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        var req = OrderRequestDto.builder()
                .instrumentId(instrumentId)
                .accountId(accountId)
                .side(SideEnum.BUY)
                .orderType(OrderTypeEnum.LIMIT)
                .quantity(new BigDecimal("12.3456"))
                .limitPrice(new BigDecimal("99.999"))
                .note("upd")
                .build();

        OrderResponseDto dto = service.update(orderId, accountId, req);

        assertThat(dto.getQuantity()).isNotNull();
        assertThat(dto.getNote()).isEqualTo("upd");
        verify(repository).save(any(OrderEntity.class));
    }

    @Test
    void update_whenNotPending_throwsIllegalState() {
        pendingOrder.setStatus(OrderStatusEnum.FILLED);
        when(repository.findByOrderIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(pendingOrder));

        var req = OrderRequestDto.builder()
                .instrumentId(instrumentId)
                .accountId(accountId)
                .side(SideEnum.SELL)
                .orderType(OrderTypeEnum.MARKET)
                .quantity(new BigDecimal("1"))
                .build();

        assertThatThrownBy(() -> service.update(orderId, accountId, req))
                .isInstanceOf(IllegalStateException.class);
        verify(repository, never()).save(any());
    }

    // ---- delete ----
    @Test
    void delete_shouldFindAndDelete() {
        when(repository.findByOrderIdAndAccountId(orderId, accountId)).thenReturn(Optional.of(pendingOrder));

        service.delete(orderId, accountId);

        verify(repository).findByOrderIdAndAccountId(orderId, accountId);
        verify(repository).delete(pendingOrder);
    }
}