package com.investment.portfolios;

import com.investment.portfolios.dto.PortfolioRequestDto;
import com.investment.portfolios.dto.PortfolioResponseDto;
import com.investment.portfolios.entity.PortfolioEntity;
import com.investment.portfolios.service.PortfolioServiceImpl;
import com.investment.portfolios.utils.enums.PortfolioStatusEnum;
import com.investment.portfolios.exception.NotFoundException;
import com.investment.portfolios.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository repository;

    @InjectMocks
    private PortfolioServiceImpl service;

    // ========= Helpers =========

    private PortfolioEntity entity(UUID id, UUID userId, String name, PortfolioStatusEnum status) {
        PortfolioEntity e = new PortfolioEntity();
        e.setId(id);
        e.setUserId(userId);
        e.setName(name);
        e.setStatus(status);
        return e;
    }

    private PortfolioRequestDto request(UUID userId, String name, PortfolioStatusEnum status) {
        return PortfolioRequestDto.builder()
                .userId(userId)
                .name(name)
                .status(status)
                .build();
    }

    // ========= CRUD =========

    @Test
    void createPortfolio_ok() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var dto = request(userId, "Growth", PortfolioStatusEnum.ACTIVE);

        // lo que repo guarda no importa, devolvemos una entidad con ID
        when(repository.save(any(PortfolioEntity.class)))
                .thenReturn(entity(id, userId, "Growth", PortfolioStatusEnum.ACTIVE));

        PortfolioResponseDto r = service.createPortfolio(dto);

        assertEquals(id, r.getId());
        assertEquals(userId, r.getUserId());
        assertEquals("Growth", r.getName());
        assertEquals(PortfolioStatusEnum.ACTIVE, r.getStatus());
        verify(repository).save(any(PortfolioEntity.class));
    }

    @Test
    void getPortfolioById_found() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(repository.findById(id))
                .thenReturn(Optional.of(entity(id, userId, "P1", PortfolioStatusEnum.ACTIVE)));

        PortfolioResponseDto r = service.getPortfolioById(id);

        assertEquals(id, r.getId());
        assertEquals("P1", r.getName());
        verify(repository).findById(id);
    }

    @Test
    void getPortfolioById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getPortfolioById(id));
    }

    @Test
    void updatePortfolio_ok() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var existing = entity(id, userId, "Old", PortfolioStatusEnum.ACTIVE);
        var dto = request(userId, "New", PortfolioStatusEnum.INACTIVE);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing); // save devuelve el mismo para este test

        PortfolioResponseDto r = service.updatePortfolio(id, dto);

        assertEquals("New", r.getName());
        assertEquals(PortfolioStatusEnum.INACTIVE, r.getStatus());
        verify(repository).findById(id);
        verify(repository).save(existing);
    }

    @Test
    void deletePortfolio_ok() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deletePortfolio(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deletePortfolio_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.deletePortfolio(id));
        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }

    // ========= Métodos del repositorio (expuestos por el service) =========

    @Test
    void findByUserId_ok() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 2);
        var e1 = entity(UUID.randomUUID(), userId, "A", PortfolioStatusEnum.ACTIVE);
        var e2 = entity(UUID.randomUUID(), userId, "B", PortfolioStatusEnum.INACTIVE);
        when(repository.findByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(e1, e2), pageable, 2));

        Page<PortfolioResponseDto> page = service.findByUserId(userId, pageable);

        assertEquals(2, page.getTotalElements());
        assertEquals("A", page.getContent().getFirst().getName());
        verify(repository).findByUserId(userId, pageable);
    }

    @Test
    void findByUserIdAndStatus_ok() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 1);
        var e1 = entity(UUID.randomUUID(), userId, "OnlyActive", PortfolioStatusEnum.ACTIVE);

        when(repository.findByUserIdAndStatus(userId, PortfolioStatusEnum.ACTIVE, pageable))
                .thenReturn(new PageImpl<>(List.of(e1), pageable, 1));

        Page<PortfolioResponseDto> page =
                service.findByUserIdAndStatus(userId, PortfolioStatusEnum.ACTIVE, pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(PortfolioStatusEnum.ACTIVE, page.getContent().getFirst().getStatus());
        verify(repository).findByUserIdAndStatus(userId, PortfolioStatusEnum.ACTIVE, pageable);
    }

    @Test
    void findByIdAndUserId_found() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var e = entity(id, userId, "Mine", PortfolioStatusEnum.ACTIVE);
        when(repository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(e));

        PortfolioResponseDto r = service.findByIdAndUserId(id, userId);

        assertEquals("Mine", r.getName());
        verify(repository).findByIdAndUserId(id, userId);
    }

    @Test
    void findByIdAndUserId_notFound_throws() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(repository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findByIdAndUserId(id, userId));
    }

    @Test
    void existsByUserIdAndNameIgnoreCase_true() {
        UUID userId = UUID.randomUUID();
        when(repository.existsByUserIdAndNameIgnoreCase(userId, "dup")).thenReturn(true);

        assertTrue(service.existsByUserIdAndNameIgnoreCase(userId, "dup"));
        verify(repository).existsByUserIdAndNameIgnoreCase(userId, "dup");
    }

    @Test
    void existsByUserIdAndNameIgnoreCase_false() {
        UUID userId = UUID.randomUUID();
        when(repository.existsByUserIdAndNameIgnoreCase(userId, "free")).thenReturn(false);

        assertFalse(service.existsByUserIdAndNameIgnoreCase(userId, "free"));
        verify(repository).existsByUserIdAndNameIgnoreCase(userId, "free");
    }
}