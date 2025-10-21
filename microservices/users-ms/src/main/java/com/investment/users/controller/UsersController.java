package com.investment.users.controller;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;


/**
 * Public REST controller exposing user-related endpoints.
 *
 * <p>Provides read and lightweight write operations suitable for public or
 * authenticated clients depending on security configuration. Endpoints are
 * prefixed with <code>/api/v1/users</code>. Input validation annotations
 * ensure basic request constraints are validated before reaching the service
 * layer.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController {

    private final UserService service;

    /**
     * Retrieve a user by UUID.
     *
     * <p>Returns 200 OK with the {@link UserResponseDto} when the user exists.
     * Throws a NotFound-like exception (handled globally) when the user is not found.</p>
     *
     * @param id the UUID of the user to retrieve
     * @return ResponseEntity with the found {@link UserResponseDto} and HTTP 200
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Public user registration (optional).
     *
     * <p>Accepts a validated {@link UserRequestDto}, creates the user and returns
     * HTTP 201 Created with a Location header pointing to the new resource.</p>
     *
     * @param request validated request body containing user information
     * @return ResponseEntity with the created {@link UserResponseDto} and HTTP 201
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    // (opcional) registro básico público; elimina si tu caso no lo permite
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = service.create(request);
        URI location = URI.create("/api/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Find a user by email (case-insensitive).
     *
     * <p>Returns an Optional-wrapped {@link UserResponseDto}. The optional will be
     * empty when no user with the provided email exists.</p>
     *
     * @param email the email to search for (case-insensitive)
     * @return ResponseEntity with an Optional containing the {@link UserResponseDto} when present
     * and HTTP 200
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    // Buscar por email (case-insensitive)
    @GetMapping(value = "/by-email", produces = "application/json")
    public ResponseEntity<Optional<UserResponseDto>> findByEmail(@RequestParam("email") @NotBlank String email) {
        return ResponseEntity.ok(service.findByEmailIgnoreCase(email));
    }

    /**
     * Check whether a user exists for the given email.
     *
     * <p>Useful for client-side validation flows (signup/login) where the client
     * needs to know if an email is already registered.</p>
     *
     * @param email the email to check (not blank)
     * @return ResponseEntity containing true if a user exists for the email, false otherwise, with HTTP 200
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    // Verificar existencia por email
    @GetMapping(value = "/exists", produces = "application/json")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam("email") @NotBlank String email) {
        return ResponseEntity.ok(service.existsByEmail(email));
    }

    /**
     * Return a paginated list of users.
     *
     * <p>Delegates pagination and sorting logic to the service layer. The
     * <code>page</code> parameter is zero-based. The <code>size</code> parameter
     * must be positive.</p>
     *
     * @param page zero-based page index (must be >= 0)
     * @param size page size (must be positive)
     * @return ResponseEntity containing a paginated slice of users and HTTP 200
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    // Listado paginado simple (orden por createdAt desc definido en service)
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.list(page, size));
    }
}