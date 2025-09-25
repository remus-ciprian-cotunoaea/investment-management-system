package com.investment.common.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorDtoTest {

    @Test
    void builderCreatesExpectedObject() {
        LocalDateTime now = LocalDateTime.now();

        ApiErrorDto dto = ApiErrorDto.builder()
                .status(404)
                .error("Not Found")
                .message("User not found")
                .path("/api/v1/users/123")
                .timestamp(now)
                .build();

        assertEquals(404, dto.getStatus());
        assertEquals("Not Found", dto.getError());
        assertEquals("User not found", dto.getMessage());
        assertEquals("/api/v1/users/123", dto.getPath());
        assertEquals(now, dto.getTimestamp());
    }

    @Test
    void equalsAndHashCodeWork() {
        ApiErrorDto a = ApiErrorDto.builder()
                .status(400).error("Bad Request").message("Invalid input").path("/test").build();

        ApiErrorDto b = ApiErrorDto.builder()
                .status(400).error("Bad Request").message("Invalid input").path("/test").build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
