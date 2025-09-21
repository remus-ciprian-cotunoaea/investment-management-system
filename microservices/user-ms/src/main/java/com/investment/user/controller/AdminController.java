package com.investment.user.controller;

import com.investment.user.dto.UserResponseDto;
import com.investment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // tu SecurityConfig ya permite /api/v1/admin/** sólo con rol
    public ResponseEntity<Page<UserResponseDto>> list(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.list(page, size));
    }
}