package com.investment.users.service;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserRequestDto request);
    UserResponseDto getById(UUID id);          // <- el test llama getById
    UserResponseDto findByEmail(String email); // <- el test llama findByEmail
    UserResponseDto update(UUID id, UserRequestDto request);
    void delete(UUID id);
    Page<UserResponseDto> list(int page, int size);
}