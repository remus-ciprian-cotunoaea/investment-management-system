package com.investment.portfolios.service;

import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.entity.PortfolioEntity;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import com.investment.portfolios.exception.NotFoundException;
import com.investment.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository repository;

    @Override
    public PortfolioResponseDto createPortfolio(PortfolioRequestDto dto) {
        var entity = new PortfolioEntity();
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());

        var saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    public PortfolioResponseDto getPortfolioById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio not found"));
        return toResponse(entity);
    }

    @Override
    public PortfolioResponseDto updatePortfolio(UUID id, PortfolioRequestDto dto) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio not found"));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        var updated = repository.save(entity);
        return toResponse(updated);
    }

    @Override
    public void deletePortfolio(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Portfolio not found");
        }
        repository.deleteById(id);
    }

    // ===== Métodos del repo =====

    @Override
    public Page<PortfolioResponseDto> findByUserId(UUID userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<PortfolioResponseDto> findByUserIdAndStatus(UUID userId, PortfolioStatusEnum status, Pageable pageable) {
        return repository.findByUserIdAndStatus(userId, status, pageable)
                .map(this::toResponse);
    }

    @Override
    public PortfolioResponseDto findByIdAndUserId(UUID id, UUID userId) {
        var entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Portfolio not found for this user"));
        return toResponse(entity);
    }

    @Override
    public boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name) {
        return repository.existsByUserIdAndNameIgnoreCase(userId, name);
    }

    // ===== Mapper simple =====
    private PortfolioResponseDto toResponse(PortfolioEntity e) {
        // usa constructor de 4 args (gracias al DTO de arriba) o builder
         return PortfolioResponseDto.builder()
                 .id(e.getId())
                 .userId(e.getUserId())
                 .name(e.getName())
                 .status(e.getStatus())
                 .build();
    }
}