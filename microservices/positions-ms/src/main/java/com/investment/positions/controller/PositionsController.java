package com.investment.positions.controller;

import com.investment.positions.dto.PositionRequestDto;
import com.investment.positions.dto.PositionResponseDto;
import com.investment.positions.service.PositionService;
import com.investment.positions.service.RecalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Positions", description = "Operations for positions read/recalculate/delete")
@RestController
@RequestMapping("/v1/positions")
@RequiredArgsConstructor
@Validated
public class PositionsController {

    private final PositionService positionService;
    private final RecalculationService recalculationService;

    @Operation(summary = "Recalculate a position (upsert by accountId + instrumentId)")
    @PostMapping(value = "/recalculate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PositionResponseDto> recalculate(@Valid @RequestBody PositionRequestDto request) {
        PositionResponseDto response = recalculationService.recalculate(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a position by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<PositionResponseDto> getById(@PathVariable("id") @NotNull UUID id) {
        PositionResponseDto response = positionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a position by accountId and instrumentId")
    @GetMapping(produces = "application/json")
    public ResponseEntity<PositionResponseDto> getByAccountAndInstrument(
            @RequestParam @NotNull UUID accountId,
            @RequestParam @NotNull UUID instrumentId) {
        PositionResponseDto response = positionService.findByAccountAndInstrument(accountId, instrumentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a position by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @NotNull UUID id) {
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}