package com.investment.users;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.entity.UserEntity;
import com.investment.users.repository.UserRepository;
import com.investment.users.service.UserServiceImpl;
import com.investment.users.utils.enums.UserStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserServiceImpl service;

    @Test
    void getById_returnsDto() {
        UUID id = UUID.randomUUID();
        OffsetDateTime ts = OffsetDateTime.parse("2025-01-01T10:00:00Z");

        UserEntity entity = UserEntity.builder()
                .id(id)
                .name("Alice")
                .email("alice@email.com")
                .status(UserStatusEnum.ACTIVE)
                .createdAt(ts.toInstant())      // <- OJO: usa OffsetDateTime en el test
                .updatedAt(ts.toInstant())
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        UserResponseDto dto = service.getById(id);

        assertEquals(id, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@email.com", dto.getEmail());
        assertEquals(UserStatusEnum.ACTIVE, dto.getStatus());
        assertEquals(ts, dto.getCreatedAt());
        assertEquals(ts, dto.getUpdatedAt());
    }

    @Test
    void create_persistsAndReturnsDto() {
        UUID fixedId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        OffsetDateTime ts = OffsetDateTime.parse("2025-01-01T10:00:00Z");

        // Ajusta el constructor/setters de tu UserRequestDto si no coincide
        UserRequestDto req = UserRequestDto.builder()
                .name("Alice")
                .email("alice@email.com")
                .status(UserStatusEnum.ACTIVE)
                .build();

        // Devolvemos una entidad "persistida"
        when(repository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity in = inv.getArgument(0);
            return UserEntity.builder()
                    .id(in.getId() != null ? in.getId() : fixedId)
                    .name(in.getName())
                    .email(in.getEmail())
                    .status(in.getStatus())
                    .createdAt(ts.toInstant())
                    .updatedAt(ts.toInstant())
                    .build();
        });

        UserResponseDto dto = service.create(req);

        assertNotNull(dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@email.com", dto.getEmail());
        assertEquals(UserStatusEnum.ACTIVE, dto.getStatus());
        assertEquals(ts, dto.getCreatedAt());
        assertEquals(ts, dto.getUpdatedAt());
    }
}