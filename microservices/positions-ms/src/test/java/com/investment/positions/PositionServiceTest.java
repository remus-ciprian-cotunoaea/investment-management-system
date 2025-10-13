package com.investment.positions;

import com.investment.common.exception.NotFoundException;
import com.investment.positions.dto.PositionResponseDto;
import com.investment.positions.entity.PositionEntity;
import com.investment.positions.repository.PositionRepository;
import com.investment.positions.service.impl.PositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository repository;

    @InjectMocks
    private PositionServiceImpl service; // SUT

    private UUID positionId;
    private UUID accountId;
    private UUID instrumentId;
    private PositionEntity entity;

    @BeforeEach
    void setup() {
        positionId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        instrumentId = UUID.randomUUID();

        entity = PositionEntity.builder()
                .positionId(positionId)
                .accountId(accountId)
                .instrumentId(instrumentId)
                .quantity(new BigDecimal("10.1234567890"))
                .avgCost(new BigDecimal("123.456789"))
                .lastUpdated(OffsetDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        void returnsDto_whenFound() {
            when(repository.findById(positionId)).thenReturn(Optional.of(entity));

            PositionResponseDto dto = service.findById(positionId);

            assertThat(dto.positionId()).isEqualTo(positionId);
            assertThat(dto.accountId()).isEqualTo(accountId);
            assertThat(dto.instrumentId()).isEqualTo(instrumentId);
            assertThat(dto.quantity()).isEqualByComparingTo("10.1234567890");
            assertThat(dto.avgCost()).isEqualByComparingTo("123.456789");
            assertThat(dto.lastUpdated()).isNotNull();

            verify(repository).findById(positionId);
            verifyNoMoreInteractions(repository);
        }

        @Test
        void throwsNotFound_whenMissing() {
            when(repository.findById(positionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(positionId))
                    .isInstanceOf(NotFoundException.class); // no validamos mensaje

            verify(repository).findById(positionId);
            verifyNoMoreInteractions(repository);
        }
    }

    @Nested
    @DisplayName("findByAccountAndInstrument")
    class FindByAccountAndInstrument {

        @Test
        void returnsDto_whenFound() {
            when(repository.findByAccountIdAndInstrumentId(accountId, instrumentId))
                    .thenReturn(Optional.of(entity));

            PositionResponseDto dto = service.findByAccountAndInstrument(accountId, instrumentId);

            assertThat(dto.positionId()).isEqualTo(positionId);
            assertThat(dto.accountId()).isEqualTo(accountId);
            assertThat(dto.instrumentId()).isEqualTo(instrumentId);

            verify(repository).findByAccountIdAndInstrumentId(accountId, instrumentId);
            verifyNoMoreInteractions(repository);
        }

        @Test
        void throwsNotFound_whenMissing() {
            when(repository.findByAccountIdAndInstrumentId(accountId, instrumentId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findByAccountAndInstrument(accountId, instrumentId))
                    .isInstanceOf(NotFoundException.class); // no validamos mensaje

            verify(repository).findByAccountIdAndInstrumentId(accountId, instrumentId);
            verifyNoMoreInteractions(repository);
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteById {

        @Test
        void deletes_whenExists() {
            when(repository.existsById(positionId)).thenReturn(true);

            service.delete(positionId);

            ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
            verify(repository).existsById(positionId);
            verify(repository).deleteById(captor.capture());
            assertThat(captor.getValue()).isEqualTo(positionId);
            verifyNoMoreInteractions(repository);
        }

        @Test
        void throwsNotFound_whenMissing() {
            when(repository.existsById(positionId)).thenReturn(false);

            assertThatThrownBy(() -> service.delete(positionId))
                    .isInstanceOf(NotFoundException.class); // no validamos mensaje

            verify(repository).existsById(positionId);
            verifyNoMoreInteractions(repository);
        }
    }
}