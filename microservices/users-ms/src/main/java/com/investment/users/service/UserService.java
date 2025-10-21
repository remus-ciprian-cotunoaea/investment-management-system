package com.investment.users.service;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.utils.enums.UserStatusEnum;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface that defines user-related business operations.
 *
 * <p>This interface centralizes domain operations for users such as create, read,
 * update and delete (CRUD), as well as searching and listing with pagination.
 * Implementations are expected to perform validation, mapping between entities
 * and DTOs, and interact with repositories or external systems as needed.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
public interface UserService {
    // CRUD

    /**
     * Create a new user from the provided request DTO.
     *
     * @param request the DTO containing fields required to create a user
     * @return the created user's data wrapped in a response DTO
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    UserResponseDto create(UserRequestDto request);

    /**
     * Retrieve a user by its unique identifier.
     *
     * @param id the UUID of the user to retrieve
     * @return the user's data as a response DTO
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    UserResponseDto getById(UUID id);

    /**
     * Update an existing user identified by id with values from the request DTO.
     * Only fields present in the DTO (or allowed by the implementation) should be updated.
     *
     * @param id the UUID of the user to update
     * @param request the DTO containing updated user fields
     * @return the updated user's data as a response DTO
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    UserResponseDto update(UUID id, UserRequestDto request);

    /**
     * Delete a user by its unique identifier.
     * Implementations should handle idempotency and any cascade or soft-delete semantics.
     *
     * @param id the UUID of the user to delete
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    void delete(UUID id);

    /**
     * Return a paginated list of users.
     *
     * @param page zero-based page index
     * @param size the size of the page to be returned
     * @return a Page of UserResponseDto containing the requested slice of users
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    Page<UserResponseDto> list(int page, int size);

    /**
     * Find a user by email address, case-insensitive.
     *
     * @param email the email to search for (case-insensitive)
     * @return an Optional containing the UserResponseDto if found, or empty if not
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    Optional<UserResponseDto> findByEmailIgnoreCase(String email);

    /**
     * Check whether a user exists for the given email.
     *
     * @param email the email to check for existence
     * @return true if a user with the provided email exists, false otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    boolean existsByEmail(String email);

    /**
     * Find users by their status with pagination.
     *
     * @param status the user status to filter by
     * @param page zero-based page index
     * @param size the size of the page to be returned
     * @return a Page of UserResponseDto matching the requested status
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    Page<UserResponseDto> findAllByStatus(UserStatusEnum status, int page, int size);

    /**
     * Find users created between two instants (inclusive) with pagination.
     *
     * @param from the inclusive start instant
     * @param to the inclusive end instant
     * @param page zero-based page index
     * @param size the size of the page to be returned
     * @return a Page of UserResponseDto created within the given time range
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    Page<UserResponseDto> findAllByCreatedAtBetween(Instant from, Instant to, int page, int size);

    /**
     * Search users by name (partial match) with pagination.
     * Implementations may search across first name, last name or a full name field.
     *
     * @param name the name or partial name to search for
     * @param page zero-based page index
     * @param size the size of the page to be returned
     * @return a Page of UserResponseDto that match the search criteria
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    Page<UserResponseDto> searchByName(String name, int page, int size);
}