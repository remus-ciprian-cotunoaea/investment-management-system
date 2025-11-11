package com.investment.portfolios.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.investment.common.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.entity.PortfolioEntity;
import com.investment.portfolios.repository.PortfolioRepository;
import com.investment.portfolios.service.PortfolioService;
import com.investment.portfolios.model.PortfolioModel;
import com.investment.portfolios.utils.DateTimeUtils;
import com.investment.portfolios.utils.NumberUtils;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import com.investment.portfolios.exception.NotFoundException;
import com.investment.portfolios.utils.Constants;

import static com.investment.portfolios.utils.NumberUtils.isNonPositive;


/**
 *
 * Implementation of the {@link PortfolioService} interface.
 *
 * <p>This service coordinates CRUD operations, paginated queries and mapping between
 * DTOs, domain models and JPA entities for portfolios. It delegates persistence
 * to the injected {@link PortfolioRepository} and provides simple validation helpers
 * used by the query methods.</p>
 *
 * Responsibilities:
 * - Create, retrieve, update and delete portfolio records.
 * - Provide paginated queries by user and by user+status.
 * - Validate incoming pageable parameters.
 * - Convert between PortfolioRequestDto, PortfolioModel and PortfolioEntity,
 *   and produce PortfolioResponseDto results (including UTC date conversions).
 * <p>
 * Note: Domain-specific exceptions such as {@code NotFoundException} and
 * {@code BadRequestException} are thrown when appropriate.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository repository;

    /**
     * Create a new instance of {@code PortfolioServiceImpl}.
     *
     * @param repository the repository used for persistence operations (injected)
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public PortfolioServiceImpl(PortfolioRepository repository) {
        this.repository = repository;
    }

    // ===== CRUD =====

    /**
     * Create a new portfolio from the provided request DTO.
     *
     * <p>Conversion flow:
     * DTO -> Model -> Entity (persist) -> Model -> Response DTO</p>
     *
     * @param dto the incoming portfolio create request; must contain required fields
     * @return the created portfolio represented as {@link PortfolioResponseDto}
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto createPortfolio(PortfolioRequestDto dto) {
        var model = toModel(dto);                 // DTO -> Model
        var saved = repository.save(toEntity(model)); // Model -> Entity (persist)
        return toResponse(toModel(saved));        // Entity -> Model -> DTO
    }

    /**
     * Retrieve a portfolio by its id.
     *
     * @param id the portfolio UUID
     * @return the portfolio as {@link PortfolioResponseDto}
     * @throws NotFoundException if no portfolio with the given id exists
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto getPortfolioById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.MESSAGE_NOT_FOUND));
        return toResponse(toModel(entity));
    }

    /**
     * Update an existing portfolio's mutable fields (name, status).
     *
     * <p>Fetches the entity by id, applies changes from the request DTO and persists
     * the updated entity.</p>
     *
     * @param id  the portfolio UUID to update
     * @param dto the request DTO containing updated values (name, status)
     * @return the updated portfolio as {@link PortfolioResponseDto}
     * @throws NotFoundException if the portfolio with the given id does not exist
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto updatePortfolio(UUID id, PortfolioRequestDto dto) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.MESSAGE_NOT_FOUND));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());

        var updated = repository.save(entity);
        return toResponse(toModel(updated));
    }

    /**
     * Delete a portfolio by id.
     *
     * <p>Performs an existence check and throws {@code NotFoundException} when the
     * portfolio does not exist.</p>
     *
     * @param id the portfolio UUID to delete
     * @throws NotFoundException if the portfolio with the given id does not exist
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public void deletePortfolio(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(Constants.MESSAGE_NOT_FOUND);
        }
        repository.deleteById(id);
    }

    // ===== Queries with page =====

    /**
     * Find portfolios by user id with pagination.
     *
     * <p>Validates the supplied {@link Pageable} and maps repository results from
     * entity -> model -> response DTO.</p>
     *
     * @param userId   the owner user UUID
     * @param pageable pagination information (page number, size, sort, ...)
     * @return a page of {@link PortfolioResponseDto} matching the user id
     * @throws BadRequestException if {@code pageable} is null or invalid
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public Page<PortfolioResponseDto> findByUserId(UUID userId, Pageable pageable) {
        validatePageable(pageable);
        return repository.findByUserId(userId, pageable)
                .map(this::toModel)
                .map(this::toResponse);
    }

    /**
     * Find portfolios by user id and status with pagination.
     *
     * <p>Validates the supplied {@link Pageable} and maps repository results from
     * entity -> model -> response DTO.</p>
     *
     * @param userId   the owner user UUID
     * @param status   the portfolio status to filter by
     * @param pageable pagination information
     * @return a page of {@link PortfolioResponseDto} filtered by user id and status
     * @throws BadRequestException if {@code pageable} is null or invalid
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public Page<PortfolioResponseDto> findByUserIdAndStatus(
            UUID userId, PortfolioStatusEnum status, Pageable pageable) {
        validatePageable(pageable);
        return repository.findByUserIdAndStatus(userId, status, pageable)
                .map(this::toModel)
                .map(this::toResponse);
    }

    /**
     * Find a portfolio by id and user id (ownership-aware lookup).
     *
     * @param id     the portfolio UUID
     * @param userId the user UUID who must own the portfolio
     * @return the portfolio as {@link PortfolioResponseDto}
     * @throws NotFoundException if no portfolio matching the id and user id exists
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto findByIdAndUserId(UUID id, UUID userId) {
        var entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(Constants.MESSAGE_NOT_FOUND_USER));
        return toResponse(toModel(entity));
    }

    /**
     * Check existence of a portfolio for a user by case-insensitive name.
     *
     * @param userId the owner user UUID
     * @param name   the portfolio name to check (case-insensitive)
     * @return true if a portfolio with the given name exists for the user
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name) {
        return repository.existsByUserIdAndNameIgnoreCase(userId, name);
    }

    // ===== Validates helpers =====

    /**
     * Validate a {@link Pageable} instance used for paginated queries.
     *
     * <p>Performs a null check and basic validation on page size and page number.
     * Throws {@link BadRequestException} when validation fails.</p>
     *
     * @param p the pageable to validate
     * @throws BadRequestException when pageable is null or has invalid values
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    private void validatePageable(Pageable p) {
        if (p == null) {
            throw new BadRequestException(Constants.MESSAGE_BAD_REQUEST);
        }
        // Uso “real” de NumberUtils para evitar “unused”
        var pageSizeAsBig = BigDecimal.valueOf(p.getPageSize());
        if (NumberUtils.isNullOrZero(pageSizeAsBig) || isNonPositive(p.getPageNumber())) {
            throw new BadRequestException(Constants.MESSAGE_BAD_REQUEST);
        }
    }

    // ===== Mappers =====

    /**
     * Map a JPA {@link PortfolioEntity} to the domain {@link PortfolioModel}.
     *
     * @param e the entity to convert (must not be null)
     * @return the created {@link PortfolioModel}
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    // Entity -> Model
    private PortfolioModel toModel(PortfolioEntity e) {
        return new PortfolioModel(
                e.getId(),
                e.getUserId(),
                e.getName(),
                e.getStatus(),
                e.getCreatedAt()
        );
    }

    /**
     * Map a request DTO {@link PortfolioRequestDto} to the domain {@link PortfolioModel}.
     *
     * <p>For new portfolios the id is left null and createdAt is set to {@link Instant#now()}.</p>
     *
     * @param dto the incoming request DTO
     * @return the created {@link PortfolioModel}
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    // DTO -> Model
    private PortfolioModel toModel(PortfolioRequestDto dto) {
        return new PortfolioModel(
                null,                 // id aún no asignado
                dto.getUserId(),
                dto.getName(),
                dto.getStatus(),
                Instant.now()         // setear createdAt
        );
    }

    /**
     * Map a domain {@link PortfolioModel} to a JPA {@link PortfolioEntity}.
     *
     * @param m the domain model to convert
     * @return a new {@link PortfolioEntity} populated from the model
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    // Model -> Entity
    private PortfolioEntity toEntity(PortfolioModel m) {
        var e = new PortfolioEntity();
        e.setId(m.id());            // si usas @GeneratedValue/UUID en la Entity, puedes dejarlo null
        e.setUserId(m.userId());
        e.setName(m.name());
        e.setStatus(m.status());
        e.setCreatedAt(m.createdAt());
        return e;    }

    /**
     * Map a domain {@link PortfolioModel} to a response DTO {@link PortfolioResponseDto}.
     *
     * <p>The createdAt timestamp is converted to an OffsetDateTime in UTC using
     * {@link DateTimeUtils#toOffsetDateTimeUTC(Instant)}.</p>
     *
     * @param m the domain model to convert
     * @return a {@link PortfolioResponseDto} ready for API responses
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    // Model -> Response DTO (usa DateTimeUtils)
    private PortfolioResponseDto toResponse(PortfolioModel m) {
        return PortfolioResponseDto.builder()
                .id(m.id())
                .userId(m.userId())
                .name(m.name())
                .status(m.status())
                .createdAt(DateTimeUtils.toOffsetDateTimeUTC(m.createdAt()))
                .build();
    }
}