package com.investment.portfolios.repository;

import com.investment.portfolios.entity.PortfolioEntity;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PortfolioEntity persistence operations.
 *
 * <p>Exposes Spring Data JPA query methods to retrieve portfolios by user,
 * filter by status, fetch a portfolio by id scoped to a user and to check
 * existence of a portfolio name for a user (case-insensitive).</p>
 *
 * <p>Method names follow Spring Data query derivation conventions so no
 * custom implementations are required for these queries.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, UUID> {

    /**
     * Retrieve a page of portfolios belonging to the specified user.
     *
     * @param userId   the UUID of the portfolio owner
     * @param pageable pagination and sorting information
     * @return a Page containing PortfolioEntity instances for the user
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    Page<PortfolioEntity> findByUserId(UUID userId, Pageable pageable);

    /**
     * Retrieve a page of portfolios for the specified user filtered by status.
     *
     * @param userId   the UUID of the portfolio owner
     * @param status   the PortfolioStatusEnum to filter portfolios by
     * @param pageable pagination and sorting information
     * @return a Page containing PortfolioEntity instances that match the user and status
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    Page<PortfolioEntity> findByUserIdAndStatus(UUID userId, PortfolioStatusEnum status, Pageable pageable);

    /**
     * Find a portfolio by its id that belongs to the specified user.
     *
     * @param id     the UUID of the portfolio
     * @param userId the UUID of the portfolio owner
     * @return an Optional containing the PortfolioEntity if present, otherwise empty
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    Optional<PortfolioEntity> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Check whether a portfolio with the given name (case-insensitive) exists for the user.
     *
     * @param userId the UUID of the user
     * @param name   the portfolio name to check (case-insensitive)
     * @return true if a portfolio with the same name exists for the user, false otherwise
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);
}