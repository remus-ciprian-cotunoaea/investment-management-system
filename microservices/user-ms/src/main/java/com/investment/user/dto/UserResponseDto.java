// src/main/java/com/investment/user/dto/UserResponseDto.java
package com.investment.user.dto;

import com.investment.user.utils.enums.UserStatusEnum;
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