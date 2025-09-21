package com.investment.user.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

import com.investment.user.utils.enums.UserStatusEnum;

/**
 * Immutable model representing a user in the business layer.
 */
@Value
@Builder
public class UserModel {

    UUID id;
    String name;
    String email;
    UserStatusEnum status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}