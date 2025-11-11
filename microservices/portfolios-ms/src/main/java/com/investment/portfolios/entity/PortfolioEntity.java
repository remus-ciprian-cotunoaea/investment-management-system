package com.investment.portfolios.entity;

import com.investment.portfolios.utils.Constants;
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
        name = Constants.PORTFOLIOS_GROUP,
        schema = Constants.PORTFOLIOS_GROUP,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = Constants.UNIQUE_CONSTRAINT,
                        columnNames = {Constants.USER_ID, Constants.PORTFOLIOS_NAME}
                )
        },
        indexes = {
                @Index(name = Constants.INDEX_PORTFOLIO_USER, columnList = Constants.USER_ID)
        }
)
public class PortfolioEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = Constants.PORTFOLIOS_ID, nullable = false, updatable = false, columnDefinition = Constants.UUID)
    private UUID id;

    @Column(name = Constants.USER_ID, nullable = false, columnDefinition = Constants.UUID)
    private UUID userId;

    @Column(name = Constants.PORTFOLIOS_NAME, nullable = false, length = Constants.HUNDRED)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.STATUS, nullable = false, length = Constants.TWENTY)
    @Builder.Default
    private PortfolioStatusEnum status = PortfolioStatusEnum.ACTIVE;

    @Column(name = Constants.CREATED_AT, nullable = false, updatable = false)
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
