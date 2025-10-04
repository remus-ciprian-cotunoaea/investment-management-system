package com.investment.orders.controller;

import com.investment.orders.dto.ExecutionResponseDto;
import com.investment.orders.dto.OrderRequestDto;
import com.investment.orders.dto.OrderResponseDto;
import com.investment.orders.service.ExecutionService;
import com.investment.orders.service.KafkaProducer;
import com.investment.orders.service.OrderService;
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

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/v1/orders", produces = "application/json")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;
    private final ExecutionService executionService;
    private final KafkaProducer kafkaProducer; // publicamos eventos

    // =========
    // ORDERS
    // =========

    @PostMapping(consumes = "application/json")
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto request) {
        var created = orderService.create(request);

        // Publica evento a Kafka (orders.order-created)
        try {
            kafkaProducer.publishOrderCreated(created);
        } catch (Exception ex) {
            // No rompemos la petición al cliente por un fallo de publicación;
            // quedará en logs / observabilidad.
            log.error("Kafka publish failed for order {}: {}", created.getId(), ex.getMessage(), ex);
        }

        URI location = URI.create("/v1/orders/" + created.getId() + "?accountId=" + created.getAccountId());
        return ResponseEntity.created(location).body(created);
    }

    /** Obtener una orden (validando que pertenezca a la cuenta) */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(
            @PathVariable UUID id,
            @RequestParam @NotNull UUID accountId
    ) {
        return ResponseEntity.ok(orderService.getByIdAndAccountId(id, accountId));
    }

    /** Listar órdenes por cuenta (paginado). Si pasas status, usa la variante con estado. */
    @GetMapping
    public ResponseEntity<?> listByAccount(
            @RequestParam @NotNull UUID accountId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) OrderStatusEnum status
    ) {
        if (status == null) {
            return ResponseEntity.ok(orderService.findAllByAccountId(accountId, page, size));
        }
        return ResponseEntity.ok(orderService.findAllByAccountIdAndStatus(accountId, status, page, size));
    }

    /** Listar órdenes por instrumento (paginado) */
    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<?> listByInstrument(
            @PathVariable UUID instrumentId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return ResponseEntity.ok(orderService.findAllByInstrumentId(instrumentId, page, size));
    }

    /** Actualizar una orden (si tu service lo soporta) */
    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderResponseDto> update(
            @PathVariable UUID id,
            @RequestParam @NotNull UUID accountId,
            @Valid @RequestBody OrderRequestDto request
    ) {
        return ResponseEntity.ok(orderService.update(id, accountId, request));
    }

    /** Cancelar/eliminar una orden (según lo que haga tu service) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestParam @NotNull UUID accountId
    ) {
        orderService.delete(id, accountId);
        return ResponseEntity.noContent().build();
    }

    // =================
    // EXECUTIONS (lecturas)
    // =================

    /** Última ejecución de una orden */
    @GetMapping("/{id}/executions/last")
    public ResponseEntity<ExecutionResponseDto> getLastExecution(@PathVariable UUID id) {
        return ResponseEntity.ok(executionService.getLastExecutionOfOrder(id));
    }

    /** Historial de ejecuciones de una orden (paginado) */
    @GetMapping("/{id}/executions")
    public ResponseEntity<?> listExecutionsByOrder(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return ResponseEntity.ok(executionService.findAllByOrder(id, page, size));
    }

    /** Historial de ejecuciones por cuenta (paginado) */
    @GetMapping("/executions")
    public ResponseEntity<?> listExecutionsByAccount(
            @RequestParam @NotNull UUID accountId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return ResponseEntity.ok(executionService.findAllByAccount(accountId, page, size));
    }
}