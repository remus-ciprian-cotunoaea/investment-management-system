package com.investment.users.controller;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.utils.enums.UserStatusEnum;
import com.investment.users.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminController {

    private final UserService service;

    // ========= CRUD =========

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = service.create(request);
        URI location = URI.create("/api/admin/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.list(page, size));
    }

    // ========= Consultas avanzadas =========

    // Por estado (findAllByStatus)
    @GetMapping(value = "/status/{status}", produces = "application/json")
    public ResponseEntity<?> findAllByStatus(@PathVariable UserStatusEnum status,
                                             @RequestParam(defaultValue = "0") @Min(0) int page,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.findAllByStatus(status, page, size));
    }

    // Rango de creación (findAllByCreatedAtBetween)
    @GetMapping(value = "/created", produces = "application/json")
    public ResponseEntity<?> findAllByCreatedAtBetween(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.findAllByCreatedAtBetween(from, to, page, size));
    }

    // Búsqueda por nombre (searchByName)
    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<?> searchByName(@RequestParam("name") @NotBlank String name,
                                          @RequestParam(defaultValue = "0") @Min(0) int page,
                                          @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.searchByName(name, page, size));
    }

    // Utilidades de correo disponibles también aquí (opcional duplicado)
    @GetMapping(value = "/by-email", produces = "application/json")
    public ResponseEntity<Optional<UserResponseDto>> findByEmail(@RequestParam("email") @NotBlank String email) {
        return ResponseEntity.ok(service.findByEmailIgnoreCase(email));
    }

    @GetMapping(value = "/exists", produces = "application/json")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam("email") @NotBlank String email) {
        return ResponseEntity.ok(service.existsByEmail(email));
    }
}