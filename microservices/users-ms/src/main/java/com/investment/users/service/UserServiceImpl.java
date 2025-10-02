package com.investment.users.service;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.NotFoundException;
import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.entity.UserEntity;
import com.investment.users.utils.enums.UserStatusEnum;
import com.investment.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    // =========================
    // CRUD
    // =========================

    @Override
    public UserResponseDto create(UserRequestDto request) {
        var e = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .status(request.getStatus())
                .build();
        e = repository.save(e);
        return toResponse(e);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        var e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("users not found"));
        return toResponse(e);
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto request) {
        var e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("users not found"));
        e.setName(request.getName());
        e.setEmail(request.getEmail());
        e.setStatus(request.getStatus());
        e = repository.save(e);
        return toResponse(e);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("users not found");
        }
        repository.deleteById(id);
    }

    // =========================
    // Listado general (paginado)
    // =========================

    @Override
    public Page<UserResponseDto> list(int page, int size) {
        if (size <= 0) throw new BadRequestException("size must be > 0");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageable).map(this::toResponse);
    }

    // =========================
    // Métodos 1:1 con el repositorio
    // =========================

    @Override
    public Optional<UserResponseDto> findByEmailIgnoreCase(String email) {
        return repository.findByEmailIgnoreCase(email).map(this::toResponse);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Page<UserResponseDto> findAllByStatus(UserStatusEnum status, int page, int size) {
        if (size <= 0) throw new BadRequestException("size must be > 0");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAllByStatus(status, pageable).map(this::toResponse);
    }

    @Override
    public Page<UserResponseDto> findAllByCreatedAtBetween(Instant from, Instant to, int page, int size) {
        if (size <= 0) throw new BadRequestException("size must be > 0");
        if (from == null || to == null) throw new BadRequestException("from/to required");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAllByCreatedAtBetween(from, to, pageable).map(this::toResponse);
    }

    @Override
    public Page<UserResponseDto> searchByName(String name, int page, int size) {
        if (size <= 0) throw new BadRequestException("size must be > 0");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAllByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
    }

    // =========================
    // Mapper
    // =========================

    private UserResponseDto toResponse(UserEntity e) {
        return UserResponseDto.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .status(e.getStatus())
                // si tu DTO incluye fechas en OffsetDateTime:
                .createdAt(e.getCreatedAt() == null ? null : e.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(e.getUpdatedAt() == null ? null : e.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .build();
    }
}