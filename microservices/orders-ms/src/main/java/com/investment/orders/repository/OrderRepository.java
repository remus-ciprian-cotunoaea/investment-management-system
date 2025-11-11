package com.investment.orders.repository;

import com.investment.orders.entity.OrderEntity;
import com.investment.orders.utils.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for managing OrderEntity instances.
 *
 * <p>Provides paginated query methods to list orders by account or instrument,
 * filter by order status, and retrieve a single order while ensuring it belongs
 * to a specific account (useful for authorization checks).</p>
 *
 * Typical usage:
 * - Inject this repository into service classes to perform CRUD and query operations.
 * - Paging is supported via {@link Pageable} for list endpoints.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    /**
     * Retrieve a page of orders that belong to the given account.
     *
     * @param accountId the UUID of the account whose orders should be returned
     * @param pageable  the paging and sorting information
     * @return a {@link Page} of {@link OrderEntity} for the account
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    Page<OrderEntity> findAllByAccountId(UUID accountId, Pageable pageable);

    /**
     * Retrieve a page of orders for the given account filtered by order status.
     *
     * @param accountId the UUID of the account whose orders should be returned
     * @param status    the {@link OrderStatusEnum} to filter by
     * @param pageable  the paging and sorting information
     * @return a {@link Page} of {@link OrderEntity} matching the account and status
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    Page<OrderEntity> findAllByAccountIdAndStatus(UUID accountId, OrderStatusEnum status, Pageable pageable);

    /**
     * Find an order by its id while ensuring it belongs to the specified account.
     *
     * <p>This method is frequently used to enforce that the requester owns the
     * order before performing operations (read/update/delete).</p>
     *
     * @param orderId   the UUID of the order
     * @param accountId the UUID of the account that must own the order
     * @return an {@link Optional} containing the {@link OrderEntity} if found and owned by the account,
     *         or {@link Optional#empty()} otherwise
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    Optional<OrderEntity> findByOrderIdAndAccountId(UUID orderId, UUID accountId);

    /**
     * Retrieve a page of orders associated with a specific instrument.
     *
     * @param instrumentId the UUID of the instrument to filter orders by
     * @param pageable     the paging and sorting information
     * @return a {@link org.springframework.data.domain.Page} of {@link OrderEntity} for the instrument
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    Page<OrderEntity> findAllByInstrumentId(UUID instrumentId, Pageable pageable);
}