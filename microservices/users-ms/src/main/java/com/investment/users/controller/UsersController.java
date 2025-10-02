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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

    private final UserService service;

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // (opcional) registro básico público; elimina si tu caso no lo permite
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = service.create(request);
        URI location = URI.create("/api/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    // Buscar por email (case-insensitive)
    @GetMapping(value = "/by-email", produces = "application/json")
    public ResponseEntity<Optional<UserResponseDto>> findByEmail(@RequestParam("email") @NotBlank String email) {
        return ResponseEntity.ok(service.findByEmailIgnoreCase(email));
    }

    // Verificar existencia por email
    @GetMapping(value = "/exists", produces = "application/json")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam("email") @NotBlank String email) {
        return ResponseEntity.ok(service.existsByEmail(email));
    }

    // Listado paginado simple (orden por createdAt desc definido en service)
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.list(page, size));
    }
}