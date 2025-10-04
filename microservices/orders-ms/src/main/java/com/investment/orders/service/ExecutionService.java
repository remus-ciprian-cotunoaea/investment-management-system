package com.investment.orders.service;

import com.investment.orders.dto.ExecutionRequestDto;
import com.investment.orders.dto.ExecutionResponseDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ExecutionService {
    // Registra una ejecución de la orden (trade)
    ExecutionResponseDto execute(ExecutionRequestDto request);

    // Historial de trades por orden
    Page<ExecutionResponseDto> findAllByOrder(UUID orderId, int page, int size);

    // Historial global por cuenta
    Page<ExecutionResponseDto> findAllByAccount(UUID accountId, int page, int size);

    // Última ejecución de una orden
    ExecutionResponseDto getLastExecutionOfOrder(UUID orderId);
}