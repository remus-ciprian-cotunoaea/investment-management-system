package com.investment.positions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investment.common.exception.BadRequestException;
import com.investment.positions.dto.PositionRequestDto;
import com.investment.positions.dto.PositionResponseDto;
import com.investment.positions.entity.PositionEntity;
import com.investment.positions.repository.PositionRepository;
import com.investment.positions.service.impl.RecalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de unidad para RecalculationServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class RecalculationServiceTest {

    @Mock
    private PositionRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecalculationServiceImpl service;

    private UUID accountId;
    private UUID instrumentId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        instrumentId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("recalculate()")
    class RecalculateTests {

        @Test
        void upsertNuevo_aplicaEscalasYDevuelveDto() {
            // given (no existe la posición)
            when(repository.findByAccountIdAndInstrumentId(accountId, instrumentId))
                    .thenReturn(Optional.empty());

            // al guardar, devolvemos la entidad con ID asignado simulando DB
            Mockito.lenient().when(repository.save(any(PositionEntity.class)))
                    .thenAnswer(invocation -> {
                        PositionEntity e = invocation.getArgument(0);
                        e.setPositionId(UUID.randomUUID());
                        // lastUpdated lo pone el service; lo respetamos
                        return e;
                    });

            var req = new PositionRequestDto(
                    accountId,
                    instrumentId,
                    new BigDecimal("12.3456789012345"), // se debe escalar a 10
                    new BigDecimal("99.1234567")         // se debe escalar a 6
            );

            // when
            PositionResponseDto resp = service.recalculate(req);

            // then
            assertThat(resp).isNotNull();
            assertThat(resp.positionId()).isNotNull();
            assertThat(resp.accountId()).isEqualTo(accountId);
            assertThat(resp.instrumentId()).isEqualTo(instrumentId);
            assertThat(resp.quantity()).isEqualByComparingTo("12.3456789012"); // escala 10
            assertThat(resp.avgCost()).isEqualByComparingTo("99.123457");      // escala 6 (HALF_UP)
            assertThat(resp.lastUpdated()).isInstanceOf(OffsetDateTime.class);

            // verify save con valores escalados
            ArgumentCaptor<PositionEntity> captor = ArgumentCaptor.forClass(PositionEntity.class);
            verify(repository).save(captor.capture());
            PositionEntity saved = captor.getValue();
            assertThat(saved.getQuantity()).isEqualByComparingTo("12.3456789012");
            assertThat(saved.getAvgCost()).isEqualByComparingTo("99.123457");
            assertThat(saved.getLastUpdated()).isNotNull();

            verify(repository).findByAccountIdAndInstrumentId(accountId, instrumentId);
            verifyNoMoreInteractions(repository);
        }

        @Test
        void cantidadCero_nulleaAvgCost() {
            // given
            when(repository.findByAccountIdAndInstrumentId(accountId, instrumentId))
                    .thenReturn(Optional.empty());
            when(repository.save(any(PositionEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var req = new PositionRequestDto(
                    accountId,
                    instrumentId,
                    new BigDecimal("0"),           // qty cero -> avgCost debe ser null
                    new BigDecimal("50.123456")
            );

            // when
            PositionResponseDto resp = service.recalculate(req);

            // then
            assertThat(resp.quantity()).isEqualByComparingTo("0E-10"); // BigDecimal con escala 10
            assertThat(resp.avgCost()).isNull();

            ArgumentCaptor<PositionEntity> captor = ArgumentCaptor.forClass(PositionEntity.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getAvgCost()).isNull();

            verify(repository).findByAccountIdAndInstrumentId(accountId, instrumentId);
            verifyNoMoreInteractions(repository);
        }

        @Test
        void lanzaBadRequest_siRequestEsNull() {
            assertThatThrownBy(() -> service.recalculate(null))
                    .isInstanceOf(BadRequestException.class);
            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("processRecalculation()")
    class ProcessRecalculationTests {

        @Test
        void parseaJsonYLlamaARecalculate() throws Exception {
            // given
            var req = new PositionRequestDto(
                    accountId, instrumentId,
                    new BigDecimal("1.0"), new BigDecimal("2.0")
            );

            String json = "{\"accountId\":\"" + accountId + "\",\"instrumentId\":\"" + instrumentId
                    + "\",\"quantity\":1.0,\"avgCost\":2.0}";
            when(objectMapper.readValue(json, PositionRequestDto.class)).thenReturn(req);

            when(repository.findByAccountIdAndInstrumentId(accountId, instrumentId))
                    .thenReturn(Optional.empty());
            when(repository.save(any(PositionEntity.class)))
                    .thenAnswer(invocation -> {
                        PositionEntity e = invocation.getArgument(0);
                        e.setPositionId(UUID.randomUUID());
                        return e;
                    });

            // when
            service.processRecalculation(json);

            // then: al menos se intentó guardar (delegó a recalculate)
            verify(repository).save(any(PositionEntity.class));
            verify(objectMapper).readValue(json, PositionRequestDto.class);
            verify(repository).findByAccountIdAndInstrumentId(accountId, instrumentId);
            verifyNoMoreInteractions(repository);
        }

        @Test
        void siFallaElParseo_noLanzaExcepcionNiInvocaRepositorio() throws Exception {
            String json = "{ malformed json";
            when(objectMapper.readValue(json, PositionRequestDto.class))
                    .thenThrow(new RuntimeException("boom"));

            // when / then (no debe propagar excepción)
            assertThatCode(() -> service.processRecalculation(json))
                    .doesNotThrowAnyException();

            verify(objectMapper).readValue(json, PositionRequestDto.class);
            verifyNoInteractions(repository);
        }
    }
}