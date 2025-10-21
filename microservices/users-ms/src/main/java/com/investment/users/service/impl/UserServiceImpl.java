package com.investment.users.service.impl;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.NotFoundException;
import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.entity.UserEntity;
import com.investment.users.utils.enums.UserStatusEnum;
import com.investment.users.exception.*;
import com.investment.users.model.UserModel;
import com.investment.users.repository.UserRepository;
import com.investment.users.service.UserService;
import com.investment.users.utils.DateTimeUtils;
import com.investment.users.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


/**
 * Implementation of {@link com.investment.users.service.UserService} providing
 * user-related business operations and mapping between entities, models and DTOs.
 *
 * <p>This service performs create, read, update and delete operations and offers
 * several query methods with pagination. It centralizes validation for page
 * size and uses {@link com.investment.users.utils.DateTimeUtils} for UTC timestamps.
 * Mapping is performed via private helper methods to keep controller/service
 * layers decoupled from persistence entities.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    // ==============================
    // CRUD
    // ==============================

    /**
     * Create a new user from the provided request DTO.
     *
     * <p>Sets createdAt and updatedAt to the current UTC instant and persists the
     * resulting {@link UserEntity} using {@link UserRepository}.</p>
     *
     * @param request the incoming request DTO containing user data
     * @return the created {@link com.investment.users.dto.UserResponseDto}
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public UserResponseDto create(UserRequestDto request) {
        final Instant now = DateTimeUtils.nowUtc(); // <- util: UTC consistente

        var e = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .status(request.getStatus())
                .createdAt(now)
                .updatedAt(now)
                .build();

        e = repository.save(e);
        return toResponse(e);
    }

    /**
     * Retrieve a user by its UUID.
     *
     * <p>Loads the entity from the repository, converts it into an intermediate
     * {@link com.investment.users.model.UserModel}, and returns a response DTO.</p>
     *
     * @param id the UUID of the user to retrieve
     * @return the {@link com.investment.users.dto.UserResponseDto} for the user
     * @throws com.investment.common.exception.NotFoundException when the user is not present
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public UserResponseDto getById(UUID id) {
        var e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("users not found"));
        // ejemplo de uso del modelo intermedio sin romper nada
        var model = toModel(e);
        return toResponse(model); // delegación (evita duplicar el mapeo)
    }

    /**
     * Update an existing user's mutable fields.
     *
     * <p>Finds the entity by id, applies changes from the request DTO, updates the
     * updatedAt UTC timestamp and persists the entity.</p>
     *
     * @param id the UUID of the user to update
     * @param request the DTO containing updated user fields
     * @return the updated {@link com.investment.users.dto.UserResponseDto}
     * @throws com.investment.common.exception.NotFoundException when the user is not present
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public UserResponseDto update(UUID id, UserRequestDto request) {
        var e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("users not found"));

        e.setName(request.getName());
        e.setEmail(request.getEmail());
        e.setStatus(request.getStatus());
        e.setUpdatedAt(DateTimeUtils.nowUtc());

        e = repository.save(e);
        return toResponse(e); // sigue funcionando igual
    }

    /**
     * Delete a user by UUID.
     *
     * <p>Performs an existence check and deletes the entity. Deletion is
     * idempotent from the caller's standpoint: a missing user results in a
     * NotFoundException.</p>
     *
     * @param id the UUID of the user to delete
     * @throws com.investment.common.exception.NotFoundException when the user is not present
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("users not found");
        }
        repository.deleteById(id);
    }

    /**
     * Return a paginated list of users sorted by createdAt descending.
     *
     * @param page zero-based page index
     * @param size page size (must be > 0)
     * @return a {@link org.springframework.data.domain.Page} of {@link com.investment.users.dto.UserResponseDto}
     * @throws com.investment.common.exception.BadRequestException when size is invalid
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public Page<UserResponseDto> list(int page, int size) {
        validatePageSize(size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageable).map(this::toResponse);
    }

    // ==============================
    // Methods 1:1 with repository
    // ==============================

    /**
     * Find a user by email (case-insensitive).
     *
     * @param email the email to search for
     * @return an Optional containing {@link com.investment.users.dto.UserResponseDto} when found
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public Optional<UserResponseDto> findByEmailIgnoreCase(String email) {
        return repository.findByEmailIgnoreCase(email).map(this::toResponse);
    }

    /**
     * Check if a user exists for the given email.
     *
     * @param email the email to check
     * @return true if a user with the email exists; false otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * Find users filtered by status with pagination.
     *
     * @param status the {@link UserStatusEnum} to filter by
     * @param page zero-based page index
     * @param size page size (must be > 0)
     * @return a paginated result of matching users
     * @throws com.investment.common.exception.BadRequestException when size is invalid
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public Page<UserResponseDto> findAllByStatus(UserStatusEnum status, int page, int size) {
        validatePageSize(size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAllByStatus(status, pageable).map(this::toResponse);
    }

    /**
     * Find users created between two instants (inclusive) with pagination.
     *
     * <p>Validates the input range and delegates to the repository. The 'from'
     * instant must be less than or equal to 'to'.</p>
     *
     * @param from inclusive start instant (UTC)
     * @param to inclusive end instant (UTC)
     * @param page zero-based page index
     * @param size page size (must be > 0)
     * @return a paginated result of users created in the given range
     * @throws com.investment.common.exception.BadRequestException when inputs are invalid
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public Page<UserResponseDto> findAllByCreatedAtBetween(Instant from, Instant to, int page, int size) {
        validatePageSize(size);
        if (from == null || to == null) throw new BadRequestException("'from/to' required");
        if (from.isAfter(to)) throw new BadRequestException("'from' must be <= 'to'");

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAllByCreatedAtBetween(from, to, pageable).map(this::toResponse);
    }

    /**
     * Search users by partial name (case-insensitive) with pagination.
     *
     * @param name the partial or full name to search for
     * @param page zero-based page index
     * @param size page size (must be > 0)
     * @return a paginated result of users whose name contains the provided value
     * @throws com.investment.common.exception.BadRequestException when size is invalid
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Override
    public Page<UserResponseDto> searchByName(String name, int page, int size) {
        validatePageSize(size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAllByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
    }

    // ==============================
    // Mapper helpers
    // ==============================

    /**
     * Map a {@link com.investment.users.model.UserModel} to a {@link com.investment.users.dto.UserResponseDto}.
     *
     * <p>Converts LocalDateTime fields to OffsetDateTime with UTC offset before
     * building the response DTO so clients receive timezone-aware timestamps.</p>
     *
     * @param m the intermediate user model
     * @return populated {@link com.investment.users.dto.UserResponseDto}
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    private UserResponseDto toResponse(UserModel m) {
        return UserResponseDto.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt() == null ? null : m.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(m.getUpdatedAt() == null ? null : m.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .build();
    }

    /**
     * Map a {@link UserEntity} to a response DTO via the intermediate model conversion.
     *
     * @param e the persistence entity
     * @return the corresponding {@link com.investment.users.dto.UserResponseDto}
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    private UserResponseDto toResponse(UserEntity e) {
        return toResponse(toModel(e));
    }

    /**
     * Convert a {@link UserEntity} to an immutable {@link UserModel}.
     *
     * <p>This method converts Instant timestamps from the entity into
     * {@link LocalDateTime} in UTC for use in the service/model layer.</p>
     *
     * @param e the persistence entity to convert
     * @return the built {@link UserModel}
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    private UserModel toModel(UserEntity e) {
        LocalDateTime created = (e.getCreatedAt() == null) ? null
                : LocalDateTime.ofInstant(e.getCreatedAt(), ZoneOffset.UTC);
        LocalDateTime updated = (e.getUpdatedAt() == null) ? null
                : LocalDateTime.ofInstant(e.getUpdatedAt(), ZoneOffset.UTC);

        return UserModel.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .status(e.getStatus())
                .createdAt(created)
                .updatedAt(updated)
                .build();
    }

    /**
     * Validate that the provided page size is positive.
     *
     * @param size the page size to validate
     * @throws com.investment.common.exception.BadRequestException when size is <= 0
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    private void validatePageSize(int size) {
        if (NumberUtils.isNonPositive(size)) {
            throw new BadRequestException("size must be > 0");
        }
    }
}