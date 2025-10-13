package com.investment.positions.service;

import com.investment.positions.dto.PositionResponseDto;

import java.util.UUID;

/** * Lectura y borrado de posiciones.
 * Mantener mínimo: read, read por (account,instrument) y delete.
 */
public interface PositionService {

    PositionResponseDto findById(UUID positionId);

    PositionResponseDto findByAccountAndInstrument(UUID accountId, UUID instrumentId);

    void delete(UUID positionId);
}