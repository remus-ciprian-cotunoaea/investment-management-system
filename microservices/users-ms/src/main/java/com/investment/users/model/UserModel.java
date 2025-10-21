package com.investment.users.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

import com.investment.users.utils.enums.UserStatusEnum;

/**
 * Immutable model representing a user in the domain layer.
 *
 * <p>This value object is intended for internal use inside the service layer
 * and for mapping data between persistence/entities and API DTOs. It is
 * immutable (Lombok {@code @Value}) and created via the generated builder
 * ({@code @Builder}). Timestamp fields use {@link java.time.LocalDateTime}
 * which represent date-time without timezone information; convert to an offset
 * or instant when exposing to clients if needed.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Value
@Builder
@Data
public class UserModel {

    UUID id;
    String name;
    String email;
    UserStatusEnum status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}