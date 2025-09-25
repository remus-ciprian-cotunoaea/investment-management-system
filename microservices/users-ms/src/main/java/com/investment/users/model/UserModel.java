package com.investment.users.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

import com.investment.users.utils.enums.UserStatusEnum;

/**
 * Immutable model representing a users in the business layer.
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