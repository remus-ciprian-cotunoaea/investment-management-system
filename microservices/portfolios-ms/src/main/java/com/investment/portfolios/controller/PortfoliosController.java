package com.investment.portfolios.controller;

import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.utils.Constants;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import com.investment.portfolios.service.PortfolioService;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;


/**
 * REST controller that exposes CRUD and query endpoints for portfolios.
 *
 * <p>This controller delegates business operations to {@link PortfolioService} and
 * translates service results into HTTP responses. Endpoints are exposed under
 * the base path {@code /api/v1/portfolios}.
 * Responsibilities:
 * <ul>
 *   <li>Create, read, update and delete portfolios.</li>
 *   <li>Query portfolios by user, status and existence by name.</li>
 * </ul>
 *
 * All request payloads are validated using Jakarta Bean Validation where applicable.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@RestController
@RequestMapping(Constants.PORTFOLIOS_BASE_PATH)
@RequiredArgsConstructor
public class PortfoliosController {

    private final PortfolioService service;

    // ===== CRUD =====

    /**
     * Create a new portfolio.
     *
     * <p>Validates the incoming {@link PortfolioRequestDto}, delegates creation to the
     * service and returns a 201 Created response with the created resource in the body
     * and a Location header pointing to the new resource.
     *
     * @param dto the portfolio data to create (validated)
     * @return ResponseEntity containing the created {@link PortfolioResponseDto} and Location header
     * @throws ConstraintViolationException when validation fails
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @PostMapping
    public ResponseEntity<PortfolioResponseDto> create(@Valid @RequestBody PortfolioRequestDto dto) {
        PortfolioResponseDto created = service.createPortfolio(dto);
        return ResponseEntity
                .created(URI.create(Constants.PORTFOLIOS_BASE_PATH + created.getId()))
                .body(created);
    }

    /**
     * Retrieve a portfolio by its identifier.
     *
     * @param id the UUID of the portfolio to retrieve
     * @return 200 OK with the {@link PortfolioResponseDto} when found
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getPortfolioById(id));
    }

    /**
     * Update an existing portfolio.
     *
     * <p>Accepts a validated {@link PortfolioRequestDto} and applies updates to the
     * portfolio identified by {@code id}. Returns the updated resource.
     *
     * @param id  the UUID of the portfolio to update
     * @param dto the new portfolio data (validated)
     * @return 200 OK with the updated {@link PortfolioResponseDto}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponseDto> update(@PathVariable UUID id,
                                                       @Valid @RequestBody PortfolioRequestDto dto) {
        return ResponseEntity.ok(service.updatePortfolio(id, dto));
    }

    /**
     * Delete a portfolio by its identifier.
     *
     * @param id the UUID of the portfolio to delete
     * @return 204 No Content when deletion succeeds
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find portfolios belonging to a user with pagination support.
     *
     * @param userId   the UUID of the user whose portfolios to retrieve
     * @param pageable pagination information (page, size, sort)
     * @return 200 OK with a page of {@link PortfolioResponseDto}
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PortfolioResponseDto>> findByUserId(@PathVariable UUID userId,
                                                                   Pageable pageable) {
        return ResponseEntity.ok(service.findByUserId(userId, pageable));
    }

    /**
     * Find portfolios for a user filtered by portfolio status with pagination.
     *
     * @param userId   the UUID of the user
     * @param status   the {@link PortfolioStatusEnum} to filter by
     * @param pageable pagination information
     * @return 200 OK with a page of {@link PortfolioResponseDto} matching the status
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<Page<PortfolioResponseDto>> findByUserIdAndStatus(@PathVariable UUID userId,
                                                                            @PathVariable PortfolioStatusEnum status,
                                                                            Pageable pageable) {
        return ResponseEntity.ok(service.findByUserIdAndStatus(userId, status, pageable));
    }

    /**
     * Find a portfolio by its id and the owning user id.
     *
     * @param id     the UUID of the portfolio
     * @param userId the UUID of the owning user
     * @return 200 OK with the {@link PortfolioResponseDto} when found
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<PortfolioResponseDto> findByIdAndUserId(@PathVariable UUID id,
                                                                  @PathVariable UUID userId) {
        return ResponseEntity.ok(service.findByIdAndUserId(id, userId));
    }

    /**
     * Check whether a portfolio with the given name exists for the user (case-insensitive).
     *
     * @param userId the UUID of the user
     * @param name   the portfolio name to check (query parameter)
     * @return 200 OK with {@code true} if a portfolio with the given name exists for the user, otherwise {@code false}
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> existsByUserIdAndName(@PathVariable UUID userId,
                                                         @RequestParam String name) {
        return ResponseEntity.ok(service.existsByUserIdAndNameIgnoreCase(userId, name));
    }
}