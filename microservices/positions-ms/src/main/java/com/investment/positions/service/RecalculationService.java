package com.investment.positions.service;

import com.investment.positions.dto.PositionRequestDto;
import com.investment.positions.dto.PositionResponseDto;

/**
 * Recalculo de posiciones: por petición HTTP y por mensaje Kafka.
 */
public interface RecalculationService {

    PositionResponseDto recalculate(PositionRequestDto request);

    void processRecalculation(String message);
}