package com.investment.orders.controller;

import com.investment.orders.dto.OrderRequestDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.service.KafkaProducer;
import com.investment.orders.service.OrderService;
import com.investment.orders.utils.Constants;
import com.investment.orders.utils.enums.OrderStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller that manages orders and provides read endpoints for executions.
 *
 * <p>This controller exposes endpoints to create, retrieve, update and delete orders,
 * as well as endpoints to read executions related to orders and accounts. It also
 * publishes order-created events to Kafka via {@link KafkaProducer} after successful
 * order creation.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = Constants.ORDERS_BASE_PATH, produces = Constants.APPLICATION_JSON)
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;

    // =========
    // ORDERS
    // =========

    /**
     * Creates a new order.
     *
     * <p>After persisting the order, this endpoint attempts to publish an order-created
     * event to Kafka; failures during publishing are logged but do not affect the HTTP response.</p>
     *
     * @param request the order creation request payload (validated)
     * @return ResponseEntity with status 201 Created and the created {@link OrderResponseDto} body;
     *         the Location header points to the created resource
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @PostMapping(consumes = Constants.APPLICATION_JSON)
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto request) {
        var created = orderService.create(request);
        try {
            kafkaProducer.publishOrderCreated(created);
        } catch (Exception ex) {
            log.error(Constants.KAFKA_FAILED_ORDER, created.getId(), ex.getMessage(), ex);
        }

        URI location = URI.create(Constants.ORDERS_BASE_PATH + created.getId() + Constants.PATH_ACCOUNT_ID + created.getAccountId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves an order by its id and account id.
     *
     * @param id the UUID of the order to retrieve
     * @param accountId the UUID of the account that owns the order (required)
     * @return ResponseEntity containing the {@link OrderResponseDto}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(
            @PathVariable UUID id,
            @RequestParam @NotNull UUID accountId
    ) {
        return ResponseEntity.ok(orderService.getByIdAndAccountId(id, accountId));
    }

    /**
     * Lists orders for a specific account, optionally filtered by status.
     *
     * @param accountId the account UUID to list orders for (required)
     * @param page zero-based page index (default from {@link Constants#ZERO})
     * @param size page size (default from {@link Constants#TWENTY})
     * @param status optional order status to filter by
     * @return ResponseEntity containing a paginated list of orders (type depends on service implementation)
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @GetMapping
    public ResponseEntity<?> listByAccount(
            @RequestParam @NotNull UUID accountId,
            @RequestParam(defaultValue = Constants.ZERO) @Min(Constants.INT_ZERO) int page,
            @RequestParam(defaultValue = Constants.TWENTY) @Min(Constants.INT_ONE) int size,
            @RequestParam(required = false) OrderStatusEnum status
    ) {
        if (status == null) {
            return ResponseEntity.ok(orderService.findAllByAccountId(accountId, page, size));
        }
        return ResponseEntity.ok(orderService.findAllByAccountIdAndStatus(accountId, status, page, size));
    }

    /**
     * Lists orders for a given instrument.
     *
     * @param instrumentId UUID of the instrument
     * @param page zero-based page index (default from {@link Constants#ZERO})
     * @param size page size (default from {@link Constants#TWENTY})
     * @return ResponseEntity containing a paginated list of orders for the instrument
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<?> listByInstrument(
            @PathVariable UUID instrumentId,
            @RequestParam(defaultValue = Constants.ZERO) @Min(Constants.INT_ZERO) int page,
            @RequestParam(defaultValue = Constants.TWENTY) @Min(Constants.INT_ONE) int size
    ) {
        return ResponseEntity.ok(orderService.findAllByInstrumentId(instrumentId, page, size));
    }

    /**
     * Updates an existing order.
     *
     * @param id the UUID of the order to update
     * @param accountId the UUID of the account that owns the order (required)
     * @param request the update request payload (validated)
     * @return ResponseEntity containing the updated {@link OrderResponseDto}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @PutMapping(path = "/{id}", consumes = Constants.APPLICATION_JSON)
    public ResponseEntity<OrderResponseDto> update(
            @PathVariable UUID id,
            @RequestParam @NotNull UUID accountId,
            @Valid @RequestBody OrderRequestDto request
    ) {
        return ResponseEntity.ok(orderService.update(id, accountId, request));
    }

    /**
     * Deletes an order.
     *
     * @param id the UUID of the order to delete
     * @param accountId the UUID of the account that owns the order (required)
     * @return ResponseEntity with 204 No Content on success
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestParam @NotNull UUID accountId
    ) {
        orderService.delete(id, accountId);
        return ResponseEntity.noContent().build();
    }
}