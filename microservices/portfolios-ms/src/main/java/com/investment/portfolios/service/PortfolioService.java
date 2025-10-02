package com.investment.portfolios.service;

import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PortfolioService {
    PortfolioResponseDto createPortfolio(PortfolioRequestDto dto);
    PortfolioResponseDto getPortfolioById(UUID id);
    PortfolioResponseDto updatePortfolio(UUID id, PortfolioRequestDto dto);
    void deletePortfolio(UUID id);

    // Métodos que reflejan el repositorio
    Page<PortfolioResponseDto> findByUserId(UUID userId, Pageable pageable);
    Page<PortfolioResponseDto> findByUserIdAndStatus(UUID userId, PortfolioStatusEnum status, Pageable pageable);
    PortfolioResponseDto findByIdAndUserId(UUID id, UUID userId);
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);
}
