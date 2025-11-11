package com.investment.portfolios.model;

import java.time.Instant;
import java.util.UUID;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;

/**
 * Domain model representing a portfolio in the service layer.
 * Immutable and used to decouple business logic from persistence.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
public record PortfolioModel(
        UUID id,
        UUID userId,
        String name,
        PortfolioStatusEnum status,
        Instant createdAt
) {}