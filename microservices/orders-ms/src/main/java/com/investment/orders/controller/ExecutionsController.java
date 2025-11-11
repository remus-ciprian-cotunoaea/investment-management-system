package com.investment.orders.controller;

import com.investment.orders.dto.ExecutionRequestDto;
import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.service.ExecutionService;
import com.investment.orders.utils.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


/**
 * REST controller that exposes endpoints to manage and query order executions.
 *
 * <p>Provides endpoints to register an execution for an order, retrieve execution
 * history by order or account, and fetch the last execution for a given order.</p>
 *
 * <p>Endpoints are rooted at {@code Constants.EXECUTIONS_BASE_PATH} and produce
 * JSON responses.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Validated
@RestController
@RequestMapping(value =Constants.EXECUTIONS_BASE_PATH, produces = Constants.APPLICATION_JSON)
@AllArgsConstructor
public class ExecutionsController {

    private final ExecutionService service;

    /**
     * Registers (executes) an order using the provided request payload.
     *
     * <p>The request payload is validated before processing. Although the mapping
     * URL contains an {@code orderId} path variable, this method accepts a
     * {@link ExecutionRequestDto} which should contain the necessary order identifier
     * and execution details.</p>
     *
     * @param request the execution request DTO; must be valid
     * @return {@link ResponseEntity} containing the created {@link ExecutionResponseDto}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @PostMapping("/orders/{orderId}")
    public ResponseEntity<ExecutionResponseDto> execute(@Valid @RequestBody ExecutionRequestDto request) {

        ExecutionResponseDto resp = service.execute(request);
        return ResponseEntity.ok(resp);
    }

    /**
     * Returns a paginated list of executions for a specific order.
     *
     * @param orderId the UUID of the order whose executions are requested
     * @param page    zero-based page index (defaults to {@link Constants#ZERO})
     * @param size    page size (defaults to {@link Constants#TWENTY})
     * @return {@link ResponseEntity} containing a {@link Page} of {@link ExecutionResponseDto}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Page<ExecutionResponseDto>> findAllByOrder(
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = Constants.ZERO) int page,
            @RequestParam(defaultValue = Constants.TWENTY) int size) {

        return ResponseEntity.ok(service.findAllByOrder(orderId, page, size));
    }

    /**
     * Retrieves the most recent execution for the specified order.
     *
     * @param orderId the UUID of the order
     * @return {@link ResponseEntity} containing the latest {@link ExecutionResponseDto}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @GetMapping("/orders/{orderId}/last")
    public ResponseEntity<ExecutionResponseDto> getLastExecutionOfOrder(
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(service.getLastExecutionOfOrder(orderId));
    }

    /**
     * Returns a paginated list of executions for a specific account across all orders.
     *
     * @param accountId the UUID of the account whose execution history is requested
     * @param page      zero-based page index (defaults to {@link Constants#ZERO})
     * @param size      page size (defaults to {@link Constants#TWENTY})
     * @return {@link ResponseEntity} containing a {@link Page} of {@link ExecutionResponseDto}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Page<ExecutionResponseDto>> findAllByAccount(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = Constants.ZERO) int page,
            @RequestParam(defaultValue = Constants.TWENTY) int size) {

        return ResponseEntity.ok(service.findAllByAccount(accountId, page, size));
    }
}