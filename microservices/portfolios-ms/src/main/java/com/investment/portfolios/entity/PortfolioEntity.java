package com.investment.portfolios.entity;

import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing a portfolio record.
 *
 * <p>Maps to the {@code portfolios.portfolios} table and enforces a database-level
 * unique constraint on the combination of {@code user_id} and {@code name}.
 * This entity stores basic portfolio metadata including the owning user, name,
 * status and a creation timestamp that is set automatically before persistence.
 *
 * <p>Use this entity in repositories and the service layer; DTOs are used for API
 * boundaries to avoid leaking persistence concerns to clients.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
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

    /**
     * JPA lifecycle callback executed before the entity is persisted.
     *
     * <p>Sets the {@code createdAt} timestamp to the current instant so newly
     * created portfolios have a persisted creation time.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
