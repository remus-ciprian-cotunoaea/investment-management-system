package com.investment.portfolios.dto;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Data Transfer Object used to receive portfolio create/update requests.
 *
 * <p>This DTO encapsulates the data required to create or update a portfolio:
 * the owning user's identifier, the portfolio name, and the portfolio status.
 * It is validated using Jakarta Bean Validation annotations and supports Lombok-generated
 * boilerplate (getters, setters, constructors, builder).
 *
 * <p>Usage:
 * - Sent by clients in HTTP request bodies when creating or updating portfolios.
 * - Converted to domain objects by the service layer before persistence.
 * Note: Field-level validation annotations are applied directly on the fields;
 * this Javadoc documents the DTO as a whole and not the individual attributes.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioRequestDto {
    @NotNull
    private UUID userId;

    @NotBlank
    private String name;

    @NotNull private
    PortfolioStatusEnum status;
}