package com.investment.users;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.NotFoundException;
import com.investment.users.dto.UserRequestDto;
import com.investment.users.dto.UserResponseDto;
import com.investment.users.entity.UserEntity;
import com.investment.users.repository.UserRepository;
import com.investment.users.service.impl.UserServiceImpl;
import com.investment.users.utils.DateTimeUtils;
import com.investment.users.utils.enums.UserStatusEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Unit tests for {@link com.investment.users.service.impl.UserServiceImpl}.
 *
 * <p>This test class verifies the behavior of the service implementation by
 * mocking the {@link com.investment.users.repository.UserRepository} and
 * statically mocking {@link com.investment.users.utils.DateTimeUtils#nowUtc()}
 * to return a fixed Instant for deterministic assertions.</p>
 *
 * <p>Tests cover CRUD operations, pagination and filtering logic, validation
 * error paths and DTO mapping.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserRepository repository;
    private UserServiceImpl service;

    private MockedStatic<DateTimeUtils> dateTimeUtilsMock;

    private static final Instant FIXED_NOW = Instant.parse("2025-01-01T10:15:30.123Z");

    /**
     * Test setup executed before each test.
     *
     * <p>Initializes a mocked repository, instantiates the service under test,
     * and statically mocks DateTimeUtils.nowUtc() to return a fixed Instant so
     * timestamps produced by the service are predictable in assertions.</p>
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        service = new UserServiceImpl(repository);
        dateTimeUtilsMock = mockStatic(DateTimeUtils.class);
        dateTimeUtilsMock.when(DateTimeUtils::nowUtc).thenReturn(FIXED_NOW);
    }

    /**
     * Test teardown executed after each test.
     *
     * <p>Closes the static mock for DateTimeUtils to avoid interference between
     * tests and to restore original static behavior.</p>
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @AfterEach
    void tearDown() {
        if (dateTimeUtilsMock != null){
            dateTimeUtilsMock.close();
        }
    }

    // ---------------------------
    // create
    // ---------------------------

    /**
     * Should persist a new user and return a correctly mapped response DTO.
     *
     * <p>Verifies that the service calls' repository. Save and that the returned
     * UserResponseDto contains expected id, name, email, status and UTC timestamps
     * derived from the fixed DateTimeUtils mock.</p>
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void create_shouldPersistAndReturnDto() {
        var req = UserRequestDto.builder()
                .name("Alice")
                .email("alice@test.com")
                .status(UserStatusEnum.ACTIVE)
                .build();

        var saved = baseEntity("Alice", "alice@test.com", UserStatusEnum.ACTIVE, FIXED_NOW, FIXED_NOW);

        when(repository.save(any(UserEntity.class))).thenReturn(saved);

        UserResponseDto dto = service.create(req);

        // verificación de mapeo
        assertEquals(saved.getId(), dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@test.com", dto.getEmail());
        assertEquals(UserStatusEnum.ACTIVE, dto.getStatus());
        assertNotNull(dto.getCreatedAt());
        assertEquals(FIXED_NOW, dto.getCreatedAt().toInstant());
        assertEquals(FIXED_NOW, dto.getUpdatedAt().toInstant());

        verify(repository, times(1)).save(any(UserEntity.class));
    }

    // ---------------------------
    // getById
    // ---------------------------

    /**
     * When the entity exists, getById should return a DTO with matching fields.
     *
     * <p>Mocks repository.findById to return an entity and asserts the service
     * returns the correctly mapped UserResponseDto.</p>
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void getById_whenFound_returnsDto() {
        var e = baseEntity("Bob", "bob@test.com", UserStatusEnum.INACTIVE, FIXED_NOW, FIXED_NOW);
        when(repository.findById(e.getId())).thenReturn(Optional.of(e));

        var dto = service.getById(e.getId());

        assertEquals(e.getId(), dto.getId());
        assertEquals("Bob", dto.getName());
    }

    /**
     * When the entity is missing, getById should throw NotFoundException.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void getById_whenMissing_throwsNotFound() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(id));
    }

    // ---------------------------
    // update
    // ---------------------------

    /**
     * Update should modify persisted entity fields and return an updated DTO.
     *
     * <p>Verifies that existing entity fields are updated, repository. Save is
     * used, and the updatedAt timestamp is taken from DateTimeUtils.nowUtc().</p>
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void update_shouldModifyAndReturnDto() {
        var existing = baseEntity("Old", "old@test.com", UserStatusEnum.INACTIVE,
                FIXED_NOW.minusSeconds(3600), FIXED_NOW.minusSeconds(3600));

        when(repository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(repository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        var req = UserRequestDto.builder()
                .name("New")
                .email("new@test.com")
                .status(UserStatusEnum.ACTIVE)
                .build();

        var dto = service.update(existing.getId(), req);

        assertEquals("New", dto.getName());
        assertEquals("new@test.com", dto.getEmail());
        assertEquals(UserStatusEnum.ACTIVE, dto.getStatus());
        assertEquals(FIXED_NOW, dto.getUpdatedAt().toInstant()); // updatedAt usa nowUtc()
    }

    /**
     * Update should throw NotFoundException when the entity does not exist.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void update_whenMissing_throwsNotFound() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        var req = UserRequestDto.builder().name("x").email("x@x.com").status(UserStatusEnum.ACTIVE).build();
        assertThrows(NotFoundException.class, () -> service.update(id, req));
    }

    // ---------------------------
    // delete
    // ---------------------------

    /**
     * delete should remove the entity when it exists.
     *
     * <p>Verifies repository.deleteById is called for an existing id.</p>
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void delete_whenExists_deletes() {
        var id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    /**
     * delete should throw NotFoundException when the entity is missing and must not call deleteById.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void delete_whenMissing_throwsNotFound() {
        var id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete(id));
        verify(repository, never()).deleteById(any());
    }

    // ---------------------------
    // list (paginado)
    // ---------------------------

    /**
     * list should throw BadRequestException when page size is non-positive.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void list_whenSizeNonPositive_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> service.list(0, 0));
    }

    /**
     * list should return a mapped page of UserResponseDto when repository returns a page.
     *
     * <p>Verifies pagination and mapping from entity to DTO.</p>
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void list_returnsMappedPage() {
        var e = baseEntity("P1", "p1@test.com", UserStatusEnum.ACTIVE, FIXED_NOW, FIXED_NOW);
        var page = new PageImpl<>(List.of(e), PageRequest.of(0, 10, Sort.by("createdAt")
                .descending()), 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<UserResponseDto> result = service.list(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("P1", result.getContent().getFirst().getName());
    }

    // ---------------------------
    // findByEmailIgnoreCase / existsByEmail
    // ---------------------------

    /**
     * findByEmailIgnoreCase should map an Optional entity to Optional DTO when present.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void findByEmailIgnoreCase_mapsOptional() {
        var e = baseEntity("Zoe", "ZOE@test.com", UserStatusEnum.ACTIVE, FIXED_NOW, FIXED_NOW);
        when(repository.findByEmailIgnoreCase("zoe@test.com")).thenReturn(Optional.of(e));

        var opt = service.findByEmailIgnoreCase("zoe@test.com");
        assertTrue(opt.isPresent());
        assertEquals("Zoe", opt.get().getName());
    }

    /**
     * existsByEmail should pass through to repository and return its boolean result.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void existsByEmail_passThrough() {
        when(repository.existsByEmail("a@b.com")).thenReturn(true);
        assertTrue(service.existsByEmail("a@b.com"));
    }

    // ---------------------------
    // findAllByStatus
    // ---------------------------

    /**
     * findAllByStatus should throw BadRequestException when size is non-positive.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void findAllByStatus_whenSizeNonPositive_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> service.findAllByStatus(UserStatusEnum.ACTIVE, 0, 0));
    }

    /**
     * findAllByStatus should return a mapped page when repository returns entities.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void findAllByStatus_returnsMappedPage() {
        var e = baseEntity("S", "s@test.com", UserStatusEnum.INACTIVE, FIXED_NOW, FIXED_NOW);
        when(repository.findAllByStatus(eq(UserStatusEnum.INACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(e)));

        var p = service.findAllByStatus(UserStatusEnum.INACTIVE, 0, 5);
        assertEquals(1, p.getTotalElements());
        assertEquals(UserStatusEnum.INACTIVE, p.getContent().getFirst().getStatus());
    }

    // ---------------------------
    // findAllByCreatedAtBetween
    // ---------------------------

    /**
     * findAllByCreatedAtBetween should throw BadRequestException when size is non-positive.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void findAllByCreatedAtBetween_whenSizeNonPositive_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> service.findAllByCreatedAtBetween(FIXED_NOW.minusSeconds(10), FIXED_NOW, 0, 0));
    }

    /**
     * findAllByCreatedAtBetween should throw BadRequestException when bounds are null.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void findAllByCreatedAtBetween_whenNullBounds_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> service.findAllByCreatedAtBetween(null, FIXED_NOW, 0, 10));
        assertThrows(BadRequestException.class,
                () -> service.findAllByCreatedAtBetween(FIXED_NOW, null, 0, 10));
    }

    /**
     * findAllByCreatedAtBetween should throw BadRequestException when from is after to.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void findAllByCreatedAtBetween_whenFromAfterTo_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> service.findAllByCreatedAtBetween(FIXED_NOW, FIXED_NOW.minusSeconds(1), 0, 10));
    }

    /**
     * findAllByCreatedAtBetween should return a mapped page when repository returns entities.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void findAllByCreatedAtBetween_returnsMappedPage() {
        var e = baseEntity("T", "t@test.com", UserStatusEnum.ACTIVE, FIXED_NOW, FIXED_NOW);
        when(repository.findAllByCreatedAtBetween(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(e)));

        var p = service.findAllByCreatedAtBetween(FIXED_NOW.minusSeconds(100), FIXED_NOW.plusSeconds(1), 0, 10);
        assertEquals(1, p.getTotalElements());
        assertEquals("T", p.getContent().getFirst().getName());
    }

    // ---------------------------
    // searchByName
    // ---------------------------

    /**
     * searchByName should throw BadRequestException when size is non-positive.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void searchByName_whenSizeNonPositive_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> service.searchByName("x", 0, 0));
    }

    /**
     * searchByName should return a mapped page when repository returns entities.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Test
    void searchByName_returnsMappedPage() {
        var e = baseEntity("Ann", "ann@test.com", UserStatusEnum.ACTIVE, FIXED_NOW, FIXED_NOW);
        when(repository.findAllByNameContainingIgnoreCase(eq("an"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(e)));

        var p = service.searchByName("an", 0, 10);
        assertEquals(1, p.getTotalElements());
        assertEquals("Ann", p.getContent().getFirst().getName());
    }

    // ---------------------------
    // helpers
    // ---------------------------

    /**
     * Build a base {@link UserEntity} used across tests.
     *
     * @param name user name
     * @param email user email
     * @param status user status enum
     * @param created created timestamp
     * @param updated updated timestamp
     * @return built {@link UserEntity} with random UUID
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    private static UserEntity baseEntity(String name, String email, UserStatusEnum status,
                                         Instant created, Instant updated) {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .status(status)
                .createdAt(created)
                .updatedAt(updated)
                .build();
    }
}