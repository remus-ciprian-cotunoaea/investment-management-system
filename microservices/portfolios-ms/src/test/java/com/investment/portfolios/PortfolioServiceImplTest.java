package com.investment.portfolios;

import com.investment.common.exception.BadRequestException;
import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.entity.PortfolioEntity;
import com.investment.portfolios.exception.NotFoundException;
import com.investment.portfolios.repository.PortfolioRepository;
import com.investment.portfolios.service.impl.PortfolioServiceImpl;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PortfolioServiceImpl}.
 * <p>
 * These tests exercise the CRUD operations, paginated queries, ownership-aware retrieval,
 * and existence checks implemented by the service. The {@link PortfolioRepository} is mocked
 * using Mockito to simulate persistence behaviors and to verify repository interactions.
 * <p>
 * Test naming follows the pattern: scenario_expectedBehavior.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 7, 2025
 */
@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository repository;

    private PortfolioServiceImpl service;

    // ====== datos comunes ======
    private final UUID userId = UUID.randomUUID();
    private final UUID portfolioId = UUID.randomUUID();
    private final Instant now = Instant.now();

    /**
     * Initialize the service under test before each test execution.
     * <p>
     * The repository is injected as a mock so tests can control repository behavior.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @BeforeEach
    void setUp() {
        service = new PortfolioServiceImpl(repository);
    }

    /**
     * Helper that builds a sample {@link PortfolioEntity} used by tests.
     * <p>
     * The returned entity has preset id, userId and createdAt fields so assertions can rely on stable values.
     *
     * @param name   the portfolio name
     * @param status the portfolio status
     * @return a pre-populated PortfolioEntity for test usage
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    private PortfolioEntity entity(String name, PortfolioStatusEnum status) {
        var e = new PortfolioEntity();
        e.setId(portfolioId);
        e.setUserId(userId);
        e.setName(name);
        e.setStatus(status);
        e.setCreatedAt(now);
        return e;
    }

    /**
     * Helper that builds a sample {@link PortfolioRequestDto} used by tests.
     * <p>
     * This method uses setters (compatible with Lombok @Data DTOs) to populate the DTO.
     *
     * @param name the portfolio name to request
     * @return a populated PortfolioRequestDto ready to be passed to service methods
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    private PortfolioRequestDto request(String name) {
        // si tu DTO es Lombok @Data (sin constructor), usa setters;
        // si no, cámbialo por builder/constructor real de tu clase.
        var dto = new PortfolioRequestDto();
        dto.setUserId(userId);
        dto.setName(name);
        dto.setStatus(PortfolioStatusEnum.ACTIVE);
        return dto;
    }

    // ====== CRUD ======

    /**
     * Verifies that creating a portfolio results in persisting an entity and mapping it back to a response DTO.
     * <p>
     * - Mocks repository.save(...) to simulate database assignment of id when missing.
     * - Asserts returned DTO fields match expected values and repository.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void createPortfolio_persistsAndMaps() {
        var dto = request("Alpha");
        // el service crea la Entity desde un Model; nosotros devolvemos "entity persistida"
        when(repository.save(any(PortfolioEntity.class))).thenAnswer(inv -> {
            PortfolioEntity in = inv.getArgument(0);
            // simula que la DB asigna el mismo id (o uno nuevo); dejamos el mismo para aserciones
            if (in.getId() == null) in.setId(portfolioId);
            return in;
        });

        PortfolioResponseDto resp = service.createPortfolio(dto);

        assertThat(resp.getId()).isNotNull();
        assertThat(resp.getUserId()).isEqualTo(userId);
        assertThat(resp.getName()).isEqualTo("Alpha");
        assertThat(resp.getStatus()).isEqualTo(PortfolioStatusEnum.ACTIVE);

        verify(repository).save(any(PortfolioEntity.class));
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies that getPortfolioById returns a mapped DTO when the repository finds the entity.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void getPortfolioById_found_returnsMappedDto() {
        when(repository.findById(eq(portfolioId))).thenReturn(Optional.of(entity("X", PortfolioStatusEnum.ACTIVE)));

        PortfolioResponseDto resp = service.getPortfolioById(portfolioId);

        assertThat(resp.getId()).isEqualTo(portfolioId);
        assertThat(resp.getStatus()).isEqualTo(PortfolioStatusEnum.ACTIVE);

        verify(repository).findById(portfolioId);
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies that getPortfolioById throws {@link NotFoundException} when the entity is absent.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void getPortfolioById_notFound_throwsNotFound() {
        when(repository.findById(eq(portfolioId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getPortfolioById(portfolioId));

        verify(repository).findById(portfolioId);
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies updatePortfolio updates mutable fields (name, status) and persists changes.
     * <p>
     * - Mocks findById to return an existing entity.
     * - Mocks save to return the passed entity.
     * - Asserts returned DTO reflects updated values.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void updatePortfolio_updatesNameAndStatus() {
        var existing = entity("Old", PortfolioStatusEnum.INACTIVE);
        when(repository.findById(eq(portfolioId))).thenReturn(Optional.of(existing));
        when(repository.save(any(PortfolioEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = request("New");

        PortfolioResponseDto resp = service.updatePortfolio(portfolioId, dto);

        assertThat(resp.getName()).isEqualTo("New");
        assertThat(resp.getStatus()).isEqualTo(PortfolioStatusEnum.ACTIVE);

        verify(repository).findById(portfolioId);
        verify(repository).save(any(PortfolioEntity.class));
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies updatePortfolio throws {@link NotFoundException} if the target entity does not exist.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void updatePortfolio_notFound_throwsNotFound() {
        when(repository.findById(eq(portfolioId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updatePortfolio(portfolioId, request("New")));

        verify(repository).findById(portfolioId);
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies deletePortfolio deletes an existing entity by id.
     * <p>
     * - Mocks existsById to return true and ensures deleteById is called.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void deletePortfolio_exists_deletes() {
        when(repository.existsById(eq(portfolioId))).thenReturn(true);

        service.deletePortfolio(portfolioId);

        verify(repository).existsById(portfolioId);
        verify(repository).deleteById(portfolioId);
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies deletePortfolio throws {@link NotFoundException} when the entity does not exist.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void deletePortfolio_notExists_throwsNotFound() {
        when(repository.existsById(eq(portfolioId))).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.deletePortfolio(portfolioId));

        verify(repository).existsById(portfolioId);
        verifyNoMoreInteractions(repository);
    }

    // ====== Queries con paginación ======

    /**
     * Verifies findByUserId maps repository page content to response DTOs when page parameters are valid.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void findByUserId_validPage_mapsEntities() {
        Pageable pageable = PageRequest.of(1, 10); // válido (pageNumber >= 1)
        PortfolioEntity e = entity("A", PortfolioStatusEnum.ACTIVE);
        Page<PortfolioEntity> page = new PageImpl<>(List.of(e), pageable, 1);

        when(repository.findByUserId(eq(userId), eq(pageable))).thenReturn(page);

        Page<PortfolioResponseDto> resp = service.findByUserId(userId, pageable);

        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().getFirst().getId()).isEqualTo(portfolioId);
        assertThat(resp.getContent().getFirst().getStatus()).isEqualTo(PortfolioStatusEnum.ACTIVE);

        verify(repository).findByUserId(userId, pageable);
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies findByUserId throws {@link BadRequestException} when pageable is invalid (e.g. page size == 0).
     * <p>
     * This test mocks a bad Pageable to trigger validation logic inside the service without constructing
     * an actual PageRequest that would raise IllegalArgumentException earlier.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void findByUserId_invalidPage_throwsBadRequest() {
        // No usar PageRequest.of(..., 0) -> lanzaría IllegalArgumentException fuera del service
        Pageable bad = mock(Pageable.class);
        when(bad.getPageSize()).thenReturn(0); // dispara NumberUtils.isNullOrZero

        assertThrows(BadRequestException.class, () -> service.findByUserId(userId, bad));

        verifyNoInteractions(repository);
    }

    /**
     * Verifies findByUserIdAndStatus maps repository results to DTOs for a valid pageable and status filter.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void findByUserIdAndStatus_validPage_mapsEntities() {
        Pageable pageable = PageRequest.of(2, 5);
        PortfolioEntity e = entity("Q", PortfolioStatusEnum.INACTIVE);
        Page<PortfolioEntity> page = new PageImpl<>(List.of(e), pageable, 1);

        when(repository.findByUserIdAndStatus(eq(userId), eq(PortfolioStatusEnum.INACTIVE), eq(pageable)))
                .thenReturn(page);

        Page<PortfolioResponseDto> resp =
                service.findByUserIdAndStatus(userId, PortfolioStatusEnum.INACTIVE, pageable);

        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().getFirst().getId()).isEqualTo(portfolioId);
        assertThat(resp.getContent().getFirst().getStatus()).isEqualTo(PortfolioStatusEnum.INACTIVE);

        verify(repository).findByUserIdAndStatus(userId, PortfolioStatusEnum.INACTIVE, pageable);
        verifyNoMoreInteractions(repository);
    }

    // ====== Get ownership-aware ======

    /**
     * Verifies findByIdAndUserId returns the DTO when the repository finds an entity matching both id and userId.
     * <p>
     * This ensures ownership checks in the service are correctly delegating to the repository and mapping results.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void findByIdAndUserId_found_returnsMappedDto() {
        when(repository.findByIdAndUserId(eq(portfolioId), eq(userId)))
                .thenReturn(Optional.of(entity("Z", PortfolioStatusEnum.ACTIVE)));

        PortfolioResponseDto resp = service.findByIdAndUserId(portfolioId, userId);

        assertThat(resp.getId()).isEqualTo(portfolioId);
        assertThat(resp.getUserId()).isEqualTo(userId);

        verify(repository).findByIdAndUserId(portfolioId, userId);
        verifyNoMoreInteractions(repository);
    }

    /**
     * Verifies findByIdAndUserId throws {@link NotFoundException} when no matching entity is found.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void findByIdAndUserId_notFound_throwsNotFound() {
        when(repository.findByIdAndUserId(eq(portfolioId), eq(userId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findByIdAndUserId(portfolioId, userId));

        verify(repository).findByIdAndUserId(portfolioId, userId);
        verifyNoMoreInteractions(repository);
    }

    // ====== Exists ======

    /**
     * Verifies existsByUserIdAndNameIgnoreCase delegates to the repository and returns the correct boolean result.
     *
     * @since November 7, 2025
     * @author Remus-Ciprian Cotunoaea
     */
    @Test
    void existsByUserIdAndNameIgnoreCase_delegatesToRepo() {
        when(repository.existsByUserIdAndNameIgnoreCase(eq(userId), eq("alpha"))).thenReturn(true);

        boolean exists = service.existsByUserIdAndNameIgnoreCase(userId, "alpha");

        assertThat(exists).isTrue();
        verify(repository).existsByUserIdAndNameIgnoreCase(userId, "alpha");
        verifyNoMoreInteractions(repository);
    }
}