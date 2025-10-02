package com.investment.users.service;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.utils.enums.UserStatusEnum;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD
    UserResponseDto create(UserRequestDto request);
    UserResponseDto getById(UUID id);
    UserResponseDto update(UUID id, UserRequestDto request);
    void delete(UUID id);
    Page<UserResponseDto> list(int page, int size);

    Optional<UserResponseDto> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    Page<UserResponseDto> findAllByStatus(UserStatusEnum status, int page, int size);
    Page<UserResponseDto> findAllByCreatedAtBetween(Instant from, Instant to, int page, int size);
    Page<UserResponseDto> searchByName(String name, int page, int size);
}