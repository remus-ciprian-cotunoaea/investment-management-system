package com.investment.user.service;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.NotFoundException;
import com.investment.user.dto.UserRequestDto;
import com.investment.user.dto.UserResponseDto;
import com.investment.user.entity.UserEntity;
import com.investment.user.repository.UserRepository;
import com.investment.user.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserResponseDto create(UserRequestDto request) {
        UserEntity e = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .status(request.getStatus())
                .build();
        e = repository.save(e);
        return toResponse(e);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        UserEntity e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found"));
        return toResponse(e);
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        UserEntity e = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("user not found"));
        return toResponse(e);
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto request) {
        UserEntity e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found"));
        e.setName(request.getName());
        e.setEmail(request.getEmail());
        e.setStatus(request.getStatus());
        e = repository.save(e);
        return toResponse(e);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("user not found");
        }
        repository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> list(int page, int size) {
        if (!NumberUtils.isPositive(size)) {
            throw new BadRequestException("size must be > 0");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageable).map(this::toResponse);
    }

    // --- mapper ---
    private UserResponseDto toResponse(UserEntity e) {
        return UserResponseDto.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .status(e.getStatus())
                .createdAt(OffsetDateTime.ofInstant(e.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.ofInstant(e.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}
