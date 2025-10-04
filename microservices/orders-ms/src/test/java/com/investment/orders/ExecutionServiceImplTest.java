package com.investment.orders;

import com.investment.common.exception.NotFoundException;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.entity.TradeEntity;
import com.investment.orders.repository.TradeRepository;
import com.investment.orders.service.ExecutionServiceImpl;
import com.investment.orders.utils.enums.TradeStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExecutionServiceImplTest {

    @Mock
    private TradeRepository repository;

    @InjectMocks
    private ExecutionServiceImpl service;

    private UUID orderId;
    private UUID accountId;
    private TradeEntity trade;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        orderId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        trade = new TradeEntity();
        trade.setOrderId(UUID.randomUUID());
        trade.setOrderId(orderId);
        trade.setAccountId(accountId);
        trade.setQuantity(new BigDecimal("2.5"));
        trade.setPrice(new BigDecimal("101.11"));
        trade.setFees(new BigDecimal("1.00"));
        trade.setTaxes(new BigDecimal("0.50"));
        trade.setExecutedAt(OffsetDateTime.now());
        trade.setStatus(TradeStatusEnum.EXECUTED);
    }

    @Test
    void getLastExecutionOfOrder_found_returnsDto() {
        when(repository.findTopByOrderIdOrderByExecutedAtDesc(orderId))
                .thenReturn(Optional.of(trade));

        ExecutionResponseDto dto = service.getLastExecutionOfOrder(orderId);

        assertThat(dto.getOrderId()).isEqualTo(orderId);
        verify(repository).findTopByOrderIdOrderByExecutedAtDesc(orderId);
    }

    @Test
    void testGetLastExecutionOfOrder_notFound() {
        UUID orderId = UUID.randomUUID();

        when(repository.findTopByOrderIdOrderByExecutedAtDesc(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLastExecutionOfOrder(orderId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByOrder_returnsPage() {
        when(repository.findAllByOrderId(eq(orderId), any()))
                .thenReturn(new PageImpl<>(List.of(trade)));

        Page<ExecutionResponseDto> page = service.findAllByOrder(orderId, 0, 20);

        assertThat(page.getTotalElements()).isEqualTo(1);
        verify(repository).findAllByOrderId(eq(orderId), any());
    }

    @Test
    void findAllByAccount_returnsPage() {
        when(repository.findAllByAccountId(eq(accountId), any()))
                .thenReturn(new PageImpl<>(List.of(trade)));

        Page<ExecutionResponseDto> page = service.findAllByAccount(accountId, 0, 20);

        assertThat(page.getContent()).hasSize(1);
        verify(repository).findAllByAccountId(eq(accountId), any());
    }
}