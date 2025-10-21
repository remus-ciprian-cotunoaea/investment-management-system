package com.investment.users.entity;

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
 * - Implements {@link java.io.Serializable} to allow serialization for caching or transport.
 * - Encapsulates common user fields such as id, name, email, creation/update timestamps and status.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 19, 2025
 */
@Entity
@Table(
        name = "users",
        schema = "users",
        indexes = {
                @Index(name = "ux_users_email", columnList = "email", unique = true)
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
    @Column(name = "user_id", updatable = false, columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @ToString.Include
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ToString.Include
    @Column(name = "email", nullable = false, unique = true, columnDefinition = "citext")
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "update_at", nullable = false)
    private Instant updatedAt = createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private UserStatusEnum status = UserStatusEnum.ACTIVE;
}