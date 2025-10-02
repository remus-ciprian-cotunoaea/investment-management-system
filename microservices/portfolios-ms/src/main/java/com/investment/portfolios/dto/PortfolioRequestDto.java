package com.investment.portfolios.dto;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PortfolioRequestDto {
    @NotNull
    private UUID userId;

    @NotBlank
    private String name;

    @NotNull private
    PortfolioStatusEnum status;
}