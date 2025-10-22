package com.investment.users.entity;

import com.investment.users.utils.Constants;
import com.investment.users.utils.enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing a user stored in the database.
 * <p>
 * This class:
 * - Is mapped as a persistent JPA entity to the `users` table.
 * - Implements {@link Serializable} to allow serialization for caching or transport.
 * - Encapsulates common user fields such as id, name, email, creation/update timestamps and status.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 19, 2025
 */
@Entity
@Table(
        name = Constants.ENTITY_SCHEMA_TABLE_NAME,
        schema = Constants.ENTITY_SCHEMA_TABLE_NAME,
        indexes = {
                @Index(name = Constants.ENTITY_INDEX_NAME, columnList = Constants.EMAIL, unique = true)
        }
)
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class UserEntity implements Serializable {

    @Id
    @Column(name = Constants.USER_ID, updatable = false, columnDefinition = Constants.UUID)
    private UUID id = UUID.randomUUID();

    @ToString.Include
    @Column(name = Constants.NAME, nullable = false, length = Constants.HUNDRED)
    private String name;

    @ToString.Include
    @Column(name = Constants.EMAIL, nullable = false, unique = true, columnDefinition = Constants.TEXT)
    private String email;

    @Column(name = Constants.CREATED_AT, nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = Constants.UPDATED_AT, nullable = false)
    private Instant updatedAt = createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.STATUS, nullable = false, length = Constants.SIXTEEN)
    private UserStatusEnum status = UserStatusEnum.ACTIVE;
}