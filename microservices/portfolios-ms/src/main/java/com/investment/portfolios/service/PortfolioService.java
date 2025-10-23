package com.investment.portfolios.service;

import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service contract defining portfolio-related business operations.
 *
 * <p>This interface declares operations for creating, retrieving, updating and deleting
 * portfolio resources as well as query methods that map to repository-like lookups.
 * Implementations are responsible for applying validation, business rules, authorization
 * checks, transactional boundaries and mapping between DTOs and persistence entities.</p>
 *
 * <p>Methods in this interface return DTOs intended for API-layer consumption; implementations
 * may throw domain-specific exceptions when resources are not found or validation fails.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
public interface PortfolioService {

    /**
     * Create a new portfolio from the provided request DTO.
     *
     * <p>Implementations are expected to validate the incoming data, apply business rules,
     * persist the portfolio and return a representation suitable for API responses.</p>
     *
     * @param dto the request DTO containing portfolio data
     * @return the created portfolio as a response DTO
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    PortfolioResponseDto createPortfolio(PortfolioRequestDto dto);

    /**
     * Retrieve a portfolio by its identifier.
     *
     * @param id the UUID of the portfolio to retrieve
     * @return the matching portfolio as a response DTO
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    PortfolioResponseDto getPortfolioById(UUID id);

    /**
     * Update an existing portfolio identified by id with the provided data.
     *
     * <p>Implementations should apply partial or full updates depending on the DTO contract,
     * handle validation and return the updated representation.</p>
     *
     * @param id  the UUID of the portfolio to update
     * @param dto the request DTO containing updated fields
     * @return the updated portfolio as a response DTO
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    PortfolioResponseDto updatePortfolio(UUID id, PortfolioRequestDto dto);

    /**
     * Delete a portfolio by its identifier.
     *
     * <p>Implementations should ensure proper authorization and cascade/soft-delete semantics
     * as required by the application rules.</p>
     *
     * @param id the UUID of the portfolio to delete
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    void deletePortfolio(UUID id);

    // Methods from repository layer

    /**
     * Retrieve a paginated list of portfolios for a specific user.
     *
     * @param userId   the UUID of the user who owns the portfolios
     * @param pageable pagination and sorting information
     * @return a Page containing PortfolioResponseDto instances for the user
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    Page<PortfolioResponseDto> findByUserId(UUID userId, Pageable pageable);

    /**
     * Retrieve a paginated list of portfolios for a specific user filtered by status.
     *
     * @param userId   the UUID of the user who owns the portfolios
     * @param status   the PortfolioStatusEnum to filter portfolios by
     * @param pageable pagination and sorting information
     * @return a Page containing PortfolioResponseDto instances that match the user and status
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    Page<PortfolioResponseDto> findByUserIdAndStatus(UUID userId, PortfolioStatusEnum status, Pageable pageable);

    /**
     * Find a portfolio by id that belongs to the specified user.
     *
     * @param id     the UUID of the portfolio
     * @param userId the UUID of the user who owns the portfolio
     * @return the matching PortfolioResponseDto if found (implementations may throw if not found)
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    PortfolioResponseDto findByIdAndUserId(UUID id, UUID userId);

    /**
     * Check whether a portfolio with the given name (case-insensitive) exists for the user.
     *
     * @param userId the UUID of the user
     * @param name   the portfolio name to check (case-insensitive)
     * @return true if a portfolio with the same name exists for the user, false otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);
}