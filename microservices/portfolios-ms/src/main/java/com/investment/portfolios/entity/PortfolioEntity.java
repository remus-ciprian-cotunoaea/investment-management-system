package com.investment.portfolios.entity;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(
        name = "portfolios",
        schema = "portfolios",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_portfolios_user_name",
                        columnNames = {"user_id", "name"}
                )
        },
        indexes = {
                @Index(name = "ix_portfolios_user", columnList = "user_id")
        }
)
public class PortfolioEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PortfolioStatusEnum status = PortfolioStatusEnum.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
