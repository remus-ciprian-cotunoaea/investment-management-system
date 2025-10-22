package com.investment.users.controller;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.utils.Constants;
import com.investment.users.utils.enums.UserStatusEnum;
import com.investment.users.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


/**
 * REST controller exposing administrative user management endpoints.
 *
 * <p>This controller provides CRUD operations and advanced queries for user
 * management that are intended to be used by administrative clients. Endpoints
 * are prefixed with <code>/api/admin/users</code>. Input validation annotations
 * ensure basic request constraints are validated before reaching the service
 * layer.</p>
 *
 * <p>Responses generally return {@link UserResponseDto} or paginated lists of
 * users wrapped inside {@link ResponseEntity}.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.ADMIN_BASE_PATH)
public class AdminController {

    private final UserService service;

    // ========= CRUD =========

    /**
     * Create a new user.
     *
     * <p>Accepts a {@link UserRequestDto} JSON payload, creates the user via the
     * {@link UserService}, and returns HTTP 201 Created with a Location header
     * pointing to the newly created resource.</p>
     *
     * @param request the request body with user details (validated)
     * @return ResponseEntity containing the created {@link UserResponseDto} with HTTP 201
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @PostMapping(consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = service.create(request);
        URI location = URI.create(Constants.ADMIN_URI_LOCATION + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieve an existing user by UUID.
     *
     * @param id the UUID of the user to retrieve
     * @return ResponseEntity containing the {@link UserResponseDto} and HTTP 200
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @GetMapping(value = "/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Update an existing user.
     *
     * <p>Accepts a validated {@link UserRequestDto} and updates the user
     * identified by the provided UUID.</p>
     *
     * @param id the UUID of the user to update
     * @param request the request body with updated user details (validated)
     * @return ResponseEntity containing the updated {@link UserResponseDto} and HTTP 200
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @PutMapping(value = "/{id}", consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    /**
     * Delete a user by UUID.
     *
     * @param id the UUID of the user to delete
     * @return ResponseEntity with HTTP 204 No Content upon successful deletion
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Return a paginated list of users.
     *
     * @param page zero-based page index (must be >= 0)
     * @param size page size (must be positive)
     * @return ResponseEntity containing a page of users (implementation dependent)
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @GetMapping(produces = Constants.APPLICATION_JSON)
    public ResponseEntity<Page<UserResponseDto>> list(
            @RequestParam(defaultValue = Constants.STR_ZERO) @Min(Constants.ZERO) int page,
            @RequestParam(defaultValue = Constants.STR_TEN) @Positive int size) {
        return ResponseEntity.ok(service.list(page, size));
    }

    // ========= Advanced queries =========

    // By status (findAllByStatus)
    /**
     * Find users filtered by status with pagination.
     *
     * @param status the {@link UserStatusEnum} to filter users by
     * @param page zero-based page index (must be >= 0)
     * @param size page size (must be positive)
     * @return ResponseEntity containing a page of users matching the status
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @GetMapping(value = "/status/{status}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<Page<UserResponseDto>> findAllByStatus(@PathVariable UserStatusEnum status,
                                             @RequestParam(defaultValue = Constants.STR_ZERO) @Min(Constants.ZERO) int page,
                                             @RequestParam(defaultValue = Constants.STR_TEN) @Positive int size) {
        return ResponseEntity.ok(service.findAllByStatus(status, page, size));
    }

    // Created range (findAllByCreatedAtBetween)
    /**
     * Find users created between two instants (inclusive) with pagination.
     *
     * <p>The 'from' and 'to' parameters are parsed as ISO date-time values.
     * Example: 2025-10-20T12:00:00Z</p>
     *
     * @param from inclusive start instant (ISO date-time)
     * @param to inclusive end instant (ISO date-time)
     * @param page zero-based page index (must be >= 0)
     * @param size page size (must be positive)
     * @return ResponseEntity containing a page of users created in the time range
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @GetMapping(value = "/created", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<Page<UserResponseDto>> findAllByCreatedAtBetween(
            @RequestParam(Constants.DATE_FROM)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(Constants.DATE_TO)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = Constants.STR_ZERO) @Min(Constants.ZERO) int page,
            @RequestParam(defaultValue = Constants.STR_TEN) @Positive int size) {
        return ResponseEntity.ok(service.findAllByCreatedAtBetween(from, to, page, size));
    }

    /**
     * Search users by (partial) name with pagination.
     *
     * @param name the name or partial name to search for (not blank)
     * @param page zero-based page index (must be >= 0)
     * @param size page size (must be positive)
     * @return ResponseEntity containing a page of users that match the name query
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @GetMapping(value = "/search-name", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<Page<UserResponseDto>> searchByName(@RequestParam(Constants.NAME) @NotBlank String name,
                                          @RequestParam(defaultValue = Constants.STR_ZERO) @Min(Constants.ZERO) int page,
                                          @RequestParam(defaultValue = Constants.STR_TEN) @Positive int size) {
        return ResponseEntity.ok(service.searchByName(name, page, size));
    }

    /**
     * Find a user by email (case-insensitive).
     *
     * @param email email to search for (not blank)
     * @return ResponseEntity containing an Optional with {@link UserResponseDto} when present
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @GetMapping(value = "/by-email", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<Optional<UserResponseDto>> findByEmail(@RequestParam(Constants.EMAIL) @NotBlank String email) {
        return ResponseEntity.ok(service.findByEmailIgnoreCase(email));
    }

    /**
     * Check if a user exists for the given email.
     *
     * @param email email to check (not blank)
     * @return ResponseEntity containing true if user exists, false otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @GetMapping(value = "/exists", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<Boolean> existsByEmail(@RequestParam(Constants.EMAIL) @NotBlank String email) {
        return ResponseEntity.ok(service.existsByEmail(email));
    }
}