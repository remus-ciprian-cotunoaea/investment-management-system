package com.investment.users;

import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.entity.UserEntity;
import com.investment.users.repository.UserRepository;
import com.investment.users.service.UserServiceImpl;
import com.investment.users.utils.enums.UserStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private UUID id;
    private UserEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        id = UUID.randomUUID();

        entity = UserEntity.builder()
                .id(id)
                .name("Alice")
                .email("alice@example.com")
                .status(UserStatusEnum.ACTIVE)
                .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2025-01-02T00:00:00Z"))
                .build();
    }

    // ========= CRUD básicos =========

    @Test
    void create_shouldReturnResponseDto() {
        var req = new UserRequestDto("Alice", "alice@example.com", UserStatusEnum.ACTIVE);
        when(repository.save(any(UserEntity.class))).thenReturn(entity);

        UserResponseDto out = service.create(req);

        assertNotNull(out);
        assertEquals("Alice", out.getName());
        assertEquals("alice@example.com", out.getEmail());
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    void getById_shouldReturn_whenFound() {
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        var out = service.getById(id);

        assertEquals(id, out.getId());
        assertEquals("Alice", out.getName());
    }

    @Test
    void update_shouldPersistChanges() {
        var req = new UserRequestDto("Bob", "bob@ex.com", UserStatusEnum.INACTIVE);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any(UserEntity.class))).thenReturn(entity);

        var out = service.update(id, req);

        assertEquals("Bob", out.getName());
        assertEquals("bob@ex.com", out.getEmail());
        assertEquals(UserStatusEnum.INACTIVE, out.getStatus());
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    void delete_shouldRemove_whenExists() {
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void list_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<UserEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        var out = service.list(0, 10);

        assertEquals(1, out.getTotalElements());
        assertEquals("Alice", out.getContent().getFirst().getName());
    }

    // ========= métodos adicionales =========

    @Test
    void existsByEmail_shouldReturnTrueFalse() {
        when(repository.existsByEmail("alice@example.com")).thenReturn(true);
        when(repository.existsByEmail("nope@example.com")).thenReturn(false);

        assertTrue(service.existsByEmail("alice@example.com"));
        assertFalse(service.existsByEmail("nope@example.com"));
    }

    @Test
    void findAllByStatus_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<UserEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(repository.findAllByStatus(eq(UserStatusEnum.ACTIVE), any(Pageable.class))).thenReturn(page);

        var out = service.findAllByStatus(UserStatusEnum.ACTIVE, 0, 5);

        assertEquals(1, out.getContent().size());
        assertEquals(UserStatusEnum.ACTIVE, out.getContent().getFirst().getStatus());
    }

    @Test
    void findAllByCreatedAtBetween_shouldReturnMappedPage() {
        var from = Instant.parse("2024-12-31T00:00:00Z");
        var to   = Instant.parse("2025-12-31T23:59:59Z");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<UserEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(repository.findAllByCreatedAtBetween(eq(from), eq(to), any(Pageable.class))).thenReturn(page);

        var out = service.findAllByCreatedAtBetween(from, to, 0, 10);

        assertEquals(1, out.getTotalElements());
        assertEquals(id, out.getContent().getFirst().getId());
    }

    @Test
    void searchByName_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<UserEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(repository.findAllByNameContainingIgnoreCase(eq("ali"), any(Pageable.class))).thenReturn(page);

        var out = service.searchByName("ali", 0, 10);

        assertEquals(1, out.getNumberOfElements());
        assertEquals("Alice", out.getContent().getFirst().getName());
    }

    @Test
    void findByEmailIgnoreCase_shouldReturnOptional() {
        when(repository.findByEmailIgnoreCase("alice@example.com")).thenReturn(Optional.of(entity));

        var hit = service.findByEmailIgnoreCase("alice@example.com");

        assertTrue(hit.isPresent());
        assertEquals("Alice", hit.get().getName());
    }
}
