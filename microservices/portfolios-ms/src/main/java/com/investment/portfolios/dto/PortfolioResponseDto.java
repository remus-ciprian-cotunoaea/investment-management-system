package com.investment.portfolios.dto;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponseDto {
    private UUID id;
    private UUID userId;
    private String name;
    private PortfolioStatusEnum status;
}