package com.investment.positions.service.impl;

import com.investment.common.exception.NotFoundException;
import com.investment.positions.dto.PositionResponseDto;
import com.investment.positions.entity.PositionEntity;
import com.investment.positions.repository.PositionRepository;
import com.investment.positions.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionServiceImpl implements PositionService {

    private final PositionRepository repository;

    @Override
    public PositionResponseDto findById(UUID positionId) {
        PositionEntity entity = repository.findById(positionId)
                .orElseThrow(() -> new NotFoundException("Position not found: " + positionId));
        return toResponse(entity);
    }

    @Override
    public PositionResponseDto findByAccountAndInstrument(UUID accountId, UUID instrumentId) {
        PositionEntity entity = repository.findByAccountIdAndInstrumentId(accountId, instrumentId)
                .orElseThrow(() -> new NotFoundException("Position not found for account "
                        + accountId + " and instrument " + instrumentId));
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(UUID positionId) {
        if (!repository.existsById(positionId)) {
            throw new NotFoundException("Position not found: " + positionId);
        }
        repository.deleteById(positionId);
    }

    private PositionResponseDto toResponse(PositionEntity e) {
        return new PositionResponseDto(
                e.getPositionId(),
                e.getAccountId(),
                e.getInstrumentId(),
                e.getQuantity(),
                e.getAvgCost(),
                e.getLastUpdated()
        );
    }
}