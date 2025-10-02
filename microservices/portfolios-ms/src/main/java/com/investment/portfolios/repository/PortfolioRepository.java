package com.investment.portfolios.repository;

import com.investment.portfolios.entity.PortfolioEntity;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, UUID> {

    // Listar portfolios de un usuario (paginado)
    Page<PortfolioEntity> findByUserId(UUID userId, Pageable pageable);

    // Listar por usuario + estado (paginado)
    Page<PortfolioEntity> findByUserIdAndStatus(UUID userId, PortfolioStatusEnum status, Pageable pageable);

    // Obtener un portfolio asegurando que pertenece al usuario
    Optional<PortfolioEntity> findByIdAndUserId(UUID id, UUID userId);

    // Validar unicidad de nombre por usuario (case-insensitive)
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);
}
