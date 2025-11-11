package com.investment.orders;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.NotFoundException;
import com.investment.orders.dto.ExecutionRequestDto;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.entity.OrderEntity;
import com.investment.orders.entity.TradeEntity;
import com.investment.orders.repository.OrderRepository;
import com.investment.orders.repository.TradeRepository;
import com.investment.orders.service.KafkaProducer;
import com.investment.orders.service.impl.ExecutionServiceImpl;
import com.investment.orders.utils.enums.OrderStatusEnum;
import com.investment.orders.utils.enums.SideEnum;
import com.investment.orders.utils.enums.OrderTypeEnum;
import com.investment.orders.utils.enums.TradeStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceImplTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private ExecutionServiceImpl service;

    // datos básicos
    private final UUID orderId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final UUID instrumentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        tradeRepository = mock(TradeRepository.class);
        orderRepository = mock(OrderRepository.class);
        kafkaProducer = mock(KafkaProducer.class);
        service = new ExecutionServiceImpl(tradeRepository, orderRepository, kafkaProducer);
    }

    @Test
    @DisplayName("execute(): guarda trade, actualiza orden a FILLED y publica evento")
    void execute_ok_filled() {
        // Orden existente (qty = 1)
        OrderEntity order = new OrderEntity();
        order.setOrderId(orderId);
        order.setAccountId(accountId);
        order.setInstrumentId(instrumentId);
        order.setQuantity(new BigDecimal("1.0000000000"));
        order.setStatus(OrderStatusEnum.PENDING);
        order.setSide(SideEnum.BUY);
        order.setOrderType(OrderTypeEnum.MARKET);
        order.setPlacedAt(Instant.now());
        when(orderRepository.findByOrderIdAndAccountId(orderId, accountId))
                .thenReturn(Optional.of(order));

        // El save del trade retorna el mismo trade con executedAt set
        TradeEntity saved = new TradeEntity();
        saved.setTradeId(UUID.randomUUID());
        saved.setOrderId(orderId);
        saved.setAccountId(accountId);
        saved.setInstrumentId(instrumentId);
        saved.setQuantity(new BigDecimal("1.0000000000"));
        saved.setPrice(new BigDecimal("2.000000"));
        saved.setFees(BigDecimal.ZERO);
        saved.setTaxes(BigDecimal.ZERO);
        saved.setExecutedAt(OffsetDateTime.now());
        saved.setStatus(TradeStatusEnum.EXECUTED);
        when(tradeRepository.save(any(TradeEntity.class))).thenReturn(saved);

        // request
        ExecutionRequestDto req = ExecutionRequestDto.builder()
                .orderId(orderId)
                .accountId(accountId)
                .quantity(new BigDecimal("1"))
                .price(new BigDecimal("2"))
                .build();

        // act
        ExecutionResponseDto resp = service.execute(req);

        // assert: mapeo básico
        assertThat(resp.getOrderId()).isEqualTo(orderId);
        assertThat(resp.getAccountId()).isEqualTo(accountId);
        assertThat(resp.getStatus()).isEqualTo(TradeStatusEnum.EXECUTED);

        // la orden pasa a FILLED
        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatusEnum.FILLED);

        // se publicó evento una sola vez
        verify(kafkaProducer, times(1)).publishTradeExecuted(any(ExecutionResponseDto.class));
    }

    @Test
    @DisplayName("execute(): quantity inválida -> BadRequest")
    void execute_invalidQuantity_throws() {
        ExecutionRequestDto req = ExecutionRequestDto.builder()
                .orderId(orderId).accountId(accountId)
                .quantity(BigDecimal.ZERO) // inválido
                .price(new BigDecimal("1"))
                .build();

        assertThatThrownBy(() -> service.execute(req))
                .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(orderRepository, tradeRepository, kafkaProducer);
    }

    @Test
    @DisplayName("execute(): order no existe -> NotFound")
    void execute_orderNotFound_throws() {
        when(orderRepository.findByOrderIdAndAccountId(orderId, accountId))
                .thenReturn(Optional.empty());

        ExecutionRequestDto req = ExecutionRequestDto.builder()
                .orderId(orderId).accountId(accountId)
                .quantity(new BigDecimal("1"))
                .price(new BigDecimal("1"))
                .build();

        assertThatThrownBy(() -> service.execute(req))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findAllByOrder(): devuelve página mapeada (size > 0)")
    void findAllByOrder_ok() {
        TradeEntity t = new TradeEntity();
        t.setTradeId(UUID.randomUUID());
        t.setOrderId(orderId);
        t.setAccountId(accountId);
        t.setInstrumentId(instrumentId);
        t.setQuantity(new BigDecimal("1.0000000000"));
        t.setPrice(new BigDecimal("2.000000"));
        t.setFees(BigDecimal.ZERO);
        t.setTaxes(BigDecimal.ZERO);
        t.setExecutedAt(OffsetDateTime.now());
        t.setStatus(TradeStatusEnum.EXECUTED);

        Page<TradeEntity> page = new PageImpl<>(List.of(t));
        when(tradeRepository.findAllByOrderId(eq(orderId), any(Pageable.class)))
                .thenReturn(page);

        Page<ExecutionResponseDto> resp = service.findAllByOrder(orderId, 0, 10);

        assertThat(resp.getTotalElements()).isEqualTo(1);
        assertThat(resp.getContent().getFirst().getOrderId()).isEqualTo(orderId);
        verify(tradeRepository).findAllByOrderId(eq(orderId), any(Pageable.class));
    }

    @Test
    @DisplayName("findAllByAccount(): size <= 0 -> BadRequest")
    void findAllByAccount_invalidSize_throws() {
        assertThatThrownBy(() -> service.findAllByAccount(accountId, 0, 0))
                .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(tradeRepository);
    }

    @Test
    @DisplayName("getLastExecutionOfOrder(): ok")
    void getLastExecutionOfOrder_ok() {
        TradeEntity t = new TradeEntity();
        t.setTradeId(UUID.randomUUID());
        t.setOrderId(orderId);
        t.setAccountId(accountId);
        t.setInstrumentId(instrumentId);
        t.setQuantity(new BigDecimal("1.0000000000"));
        t.setPrice(new BigDecimal("2.000000"));
        t.setFees(BigDecimal.ZERO);
        t.setTaxes(BigDecimal.ZERO);
        t.setExecutedAt(OffsetDateTime.now());
        t.setStatus(TradeStatusEnum.EXECUTED);
        when(tradeRepository.findTopByOrderIdOrderByExecutedAtDesc(orderId))
                .thenReturn(Optional.of(t));

        ExecutionResponseDto resp = service.getLastExecutionOfOrder(orderId);

        assertThat(resp.getOrderId()).isEqualTo(orderId);
        verify(tradeRepository).findTopByOrderIdOrderByExecutedAtDesc(orderId);
    }
}
