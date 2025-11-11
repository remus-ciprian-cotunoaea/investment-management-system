package com.investment.orders.repository;

import com.investment.orders.entity.TradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for persisting and querying {@link TradeEntity} records.
 *
 * <p>Provides paginated queries to list trades by order or account, and a convenience
 * method to obtain the most recent trade for a given order.</p>
 *
 * <p>Typical usage: inject this repository into service classes to perform read operations
 * and to enforce ownership/visibility constraints in higher layers.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, UUID> {

    /**
     * Retrieve a page of trades that belong to the specified order.
     *
     * @param orderId  the UUID of the order whose trades should be returned
     * @param pageable the paging (and optional sorting) parameters
     * @return a {@link Page} of {@link TradeEntity} for the order
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    Page<TradeEntity> findAllByOrderId(UUID orderId, Pageable pageable);

    /**
     * Retrieve a page of trades associated with the specified account.
     *
     * @param accountId the UUID of the account whose trades should be returned
     * @param pageable  the paging (and optional sorting) parameters
     * @return a {@link Page} of {@link TradeEntity} for the account
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    Page<TradeEntity> findAllByAccountId(UUID accountId, Pageable pageable);

    /**
     * Find the most recent trade for the given order, ordered by execution timestamp descending.
     *
     * @param orderId the UUID of the order
     * @return an {@link Optional} containing the latest {@link TradeEntity} if present, otherwise empty
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    Optional<TradeEntity> findTopByOrderIdOrderByExecutedAtDesc(UUID orderId);
}