package com.investment.portfolios.model;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


/**
 * Domain model representing an investment portfolio owned by a user.
 *
 * <p>This model carries the portfolio's unique identifier, the owner's user id,
 * a human-friendly name, its current lifecycle status, and the creation timestamp.
 * Instances are typically constructed via Lombok's builder or generated constructors
 * and used to transfer portfolio data between service layers and API endpoints.</p>
 *
 * <p>Note: Lombok annotations provide the boilerplate (getters, setters, builder,
 * and constructors) so this class focuses on the data shape only.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioModel {
    private UUID id;
    private UUID userId;
    private String name;
    private PortfolioStatusEnum status;
    private Instant createdAt;
}