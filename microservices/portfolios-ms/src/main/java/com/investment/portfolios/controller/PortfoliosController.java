package com.investment.portfolios.controller;

import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import com.investment.portfolios.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfoliosController {

    private final PortfolioService service;

    // ===== CRUD =====

    @PostMapping
    public ResponseEntity<PortfolioResponseDto> create(@Valid @RequestBody PortfolioRequestDto dto) {
        PortfolioResponseDto created = service.createPortfolio(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/portfolios/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getPortfolioById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponseDto> update(@PathVariable UUID id,
                                                       @Valid @RequestBody PortfolioRequestDto dto) {
        return ResponseEntity.ok(service.updatePortfolio(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Métodos adicionales (repositorio) =====

    // Listar portfolios por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PortfolioResponseDto>> findByUserId(@PathVariable UUID userId,
                                                                   Pageable pageable) {
        return ResponseEntity.ok(service.findByUserId(userId, pageable));
    }

    // Listar portfolios por usuario y status
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<Page<PortfolioResponseDto>> findByUserIdAndStatus(@PathVariable UUID userId,
                                                                            @PathVariable PortfolioStatusEnum status,
                                                                            Pageable pageable) {
        return ResponseEntity.ok(service.findByUserIdAndStatus(userId, status, pageable));
    }

    // Obtener un portfolio específico por id y userId
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<PortfolioResponseDto> findByIdAndUserId(@PathVariable UUID id,
                                                                  @PathVariable UUID userId) {
        return ResponseEntity.ok(service.findByIdAndUserId(id, userId));
    }

    // Validar unicidad de nombre para un usuario
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> existsByUserIdAndName(@PathVariable UUID userId,
                                                         @RequestParam String name) {
        return ResponseEntity.ok(service.existsByUserIdAndNameIgnoreCase(userId, name));
    }
}