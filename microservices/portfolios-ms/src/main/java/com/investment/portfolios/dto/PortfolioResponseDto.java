package com.investment.portfolios.dto;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import lombok.*;

import java.util.UUID;

/**
 * Response Data Transfer Object for portfolio resources.
 *
 * <p>Represents the data returned to clients for portfolio read operations.
 * This DTO carries the portfolio identifier, the owning user's identifier,
 * the portfolio name and its status. It is intended for read-only use and
 * is typically produced by the service layer and returned by controllers.
 *
 * <p>Lombok annotations generate standard constructors, getters/setters and a builder.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponseDto {

    private UUID id;
    private UUID userId;
    private String name;
    private PortfolioStatusEnum status;
}