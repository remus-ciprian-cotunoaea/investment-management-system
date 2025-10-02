package com.investment.portfolios.model;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

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