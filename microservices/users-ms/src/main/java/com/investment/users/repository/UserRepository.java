package com.investment.users.repository;


import com.investment.users.entity.UserEntity;
import com.investment.users.utils.enums.UserStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing UserEntity persistence.
 * <p>
 * Provides query methods for authentication, existence checks,
 * pagination-based listing, time-range reporting and case-insensitive search.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 19, 2025
 */
@Repository
public interface UserRepository  extends JpaRepository<UserEntity, UUID>{

    /**
     * Find a user by email (case-insensitive).
     * <p>
     * Typically used for login and registration flows.
     *
     * @param email the email address to search for (case-insensitive)
     * @return an Optional containing the matching UserEntity if found
     * @author Remus-Ciprian Cotunoaea
     * @since October 19, 2025
     */
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    /**
     * Check whether a user already exists by email.
     * <p>
     * Useful for validation during registration or updates.
     *
     * @param email the email address to check
     * @return true if a user with the given email exists, false otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 19, 2025
     */
    boolean existsByEmail(String email);

    /**
     * List users by status with pagination.
     * <p>
     * Example statuses: ACTIVE, INACTIVE. Results are paginated using the provided Pageable.
     *
     * @param status the UserStatusEnum to filter by
     * @param pageable pagination and sorting information
     * @return a Page of UserEntity matching the given status
     * @author Remus-Ciprian Cotunoaea
     * @since October 19, 2025
     */
    Page<UserEntity> findAllByStatus(UserStatusEnum status, Pageable pageable);

    /**
     * Retrieve users created within a given time range with pagination.
     * <p>
     * Useful for reporting and auditing use cases.
     *
     * @param from the start Instant (inclusive)
     * @param to the end Instant (inclusive)
     * @param pageable pagination and sorting information
     * @return a Page of UserEntity created between the specified instants
     * @author Remus-Ciprian Cotunoaea
     * @since October 19, 2025
     */
    Page<UserEntity> findAllByCreatedAtBetween(Instant from, Instant to, Pageable pageable);

    /**
     * Search users by partial name (case-insensitive) with pagination.
     * <p>
     * Useful for admin UI search features where partial matches are desired.
     *
     * @param name the partial name to search for (case-insensitive)
     * @param pageable pagination and sorting information
     * @return a Page of UserEntity whose names contain the given substring
     * @author Remus-Ciprian Cotunoaea
     * @since October 19, 2025
     */
    Page<UserEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
