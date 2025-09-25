package com.investment.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO used for returning standardized error responses from the API.
 *
 * @author Remus Ciprian Cotunoaea
 * @since September 16, 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorDto {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;
    private String error;
    private String message;
    private String path;
}