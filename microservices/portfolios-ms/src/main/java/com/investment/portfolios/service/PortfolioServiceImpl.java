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

/**
 * Service implementation that provides portfolio-related business operations.
 *
 * <p>This class implements the {@code PortfolioService} contract and coordinates
 * persistence via {@code PortfolioRepository}. It performs simple mapping between
 * DTOs and entities, applies not-found checks and delegates pagination queries
 * to the repository.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository repository;

    /**
     * Create a new portfolio from the provided request DTO.
     *
     * <p>The implementation constructs a new {@code PortfolioEntity}, copies
     * relevant fields from the request DTO, persists the entity and returns
     * a response DTO representation.</p>
     *
     * @param dto the incoming request DTO containing portfolio data
     * @return the created portfolio represented as {@code PortfolioResponseDto}
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto createPortfolio(PortfolioRequestDto dto) {
        var entity = new PortfolioEntity();
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());

        var saved = repository.save(entity);
        return toResponse(saved);
    }

    /**
     * Retrieve a portfolio by its identifier.
     *
     * <p>If the portfolio is not found, a {@code NotFoundException} is thrown.</p>
     *
     * @param id the UUID of the portfolio to retrieve
     * @return the matching portfolio represented as {@code PortfolioResponseDto}
     * @throws NotFoundException if the portfolio does not exist
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto getPortfolioById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio not found"));
        return toResponse(entity);
    }

    /**
     * Update an existing portfolio identified by id with the provided DTO.
     *
     * <p>Performs an existence check and applies the updated fields before saving.</p>
     *
     * @param id  the UUID of the portfolio to update
     * @param dto the request DTO containing updated fields
     * @return the updated portfolio as {@code PortfolioResponseDto}
     * @throws NotFoundException if the portfolio does not exist
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto updatePortfolio(UUID id, PortfolioRequestDto dto) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio not found"));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        var updated = repository.save(entity);
        return toResponse(updated);
    }

    /**
     * Delete a portfolio by its identifier.
     *
     * <p>Checks existence first and throws {@code NotFoundException} if the
     * portfolio is not present; otherwise delegates deletion to the repository.</p>
     *
     * @param id the UUID of the portfolio to delete
     * @throws NotFoundException if the portfolio does not exist
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public void deletePortfolio(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Portfolio not found");
        }
        repository.deleteById(id);
    }

    // ===== Métodos del repo =====

    /**
     * Retrieve a paginated list of portfolios for a specific user.
     *
     * @param userId   the UUID of the user who owns the portfolios
     * @param pageable pagination and sorting information
     * @return a Page of {@code PortfolioResponseDto} for the requested user
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public Page<PortfolioResponseDto> findByUserId(UUID userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    /**
     * Retrieve a paginated list of portfolios for a specific user filtered by status.
     *
     * @param userId   the UUID of the user who owns the portfolios
     * @param status   the status used to filter portfolios
     * @param pageable pagination and sorting information
     * @return a Page of {@code PortfolioResponseDto} matching the user and status
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public Page<PortfolioResponseDto> findByUserIdAndStatus(UUID userId, PortfolioStatusEnum status, Pageable pageable) {
        return repository.findByUserIdAndStatus(userId, status, pageable)
                .map(this::toResponse);
    }

    /**
     * Find a portfolio by id that belongs to the specified user.
     *
     * <p>Throws {@code NotFoundException} if the portfolio is not found or does not belong
     * to the given user.</p>
     *
     * @param id     the UUID of the portfolio
     * @param userId the UUID of the user who should own the portfolio
     * @return the matching {@code PortfolioResponseDto}
     * @throws NotFoundException if the portfolio is not found for this user
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public PortfolioResponseDto findByIdAndUserId(UUID id, UUID userId) {
        var entity = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Portfolio not found for this user"));
        return toResponse(entity);
    }

    /**
     * Check whether a portfolio with the given name (case-insensitive) exists for the user.
     *
     * @param userId the UUID of the user
     * @param name   the portfolio name to check (case-insensitive)
     * @return {@code true} if a portfolio with the same name exists for the user, {@code false} otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Override
    public boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name) {
        return repository.existsByUserIdAndNameIgnoreCase(userId, name);
    }

    // ===== Mapper simple =====

    /**
     * Map a {@code PortfolioEntity} to its corresponding {@code PortfolioResponseDto}.
     *
     * @param e the entity to map
     * @return the mapped response DTO
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    private PortfolioResponseDto toResponse(PortfolioEntity e) {
        return PortfolioResponseDto.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .name(e.getName())
                .status(e.getStatus())
                .build();
    }
}