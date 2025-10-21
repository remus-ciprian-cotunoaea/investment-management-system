// src/main/java/com/investment/users/dto/UserResponseDto.java
package com.investment.users.dto;

import com.investment.users.utils.enums.UserStatusEnum;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO returned by the API that represents user data sent to clients.
 *
 * <p>This response DTO exposes a read-only view of the user entity suitable for
 * presentation layers and external consumers. Fields may be derived or mapped
 * from domain entities. The timestamp fields use OffsetDateTime to preserve
 * timezone/offset information and map well to timestamptz in the database.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private UUID id;

    private String name;

    private String email;

    private UserStatusEnum status;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}