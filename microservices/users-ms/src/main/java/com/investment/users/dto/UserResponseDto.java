// src/main/java/com/investment/users/dto/UserResponseDto.java
package com.investment.users.dto;

import com.investment.users.utils.enums.UserStatusEnum;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private UUID id;
    private String name;
    private String email;
    private UserStatusEnum status;

    // Usamos OffsetDateTime para zona/offset; mapea bien a timestamptz
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}