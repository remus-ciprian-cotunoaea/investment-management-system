package com.investment.portfolios.exception;

import com.investment.common.dto.ApiErrorDto;
import com.investment.common.exception.BusinessException;
import com.investment.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global REST exception handler that converts application exceptions into consistent HTTP API error responses.
 *
 * <p>This controller advice catches application-specific and generic exceptions and maps them to
 * {@link ApiErrorDto} responses with appropriate HTTP status codes:
 * - {@link NotFoundException} -> 404 Not Found
 * - {@link BusinessException} -> 400 Bad Request
 * - any other {@link Exception} -> 500 Internal Server Error
 *
 * <p>The handler extracts the exception message and the incoming request path to populate the API error body.
 * This centralizes error translation so controllers and services can throw domain exceptions without coupling
 * to HTTP concerns.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle NotFoundException and return a 404 Not Found response.
     *
     * <p>Builds an {@link ApiErrorDto} containing:
     * - numeric HTTP status (404)
     * - short error label ("Not Found")
     * - the exception message
     * - the request URI that caused the exception
     *
     * @param ex the NotFoundException thrown by the application
     * @param request the current HTTP request (used to obtain the request path)
     * @return a ResponseEntity with status 404 and an ApiErrorDto body describing the error
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        ApiErrorDto body = ApiErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handle BusinessException and return a 400 Bad Request response.
     *
     * <p>This maps domain/business-level validation or rule violations to a standard API error payload.
     * The returned ApiErrorDto includes the status (400), an error label, the exception message and the request path.
     *
     * @param ex the BusinessException thrown by the application
     * @param request the current HTTP request (used to obtain the request path)
     * @return a ResponseEntity with status 400 and an ApiErrorDto body describing the error
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDto> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ApiErrorDto body = ApiErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handle any uncaught Exception and return a 500 Internal Server Error response.
     *
     * <p>This is a fallback handler that ensures unexpected exceptions are reported to API clients
     * in a consistent format. The message provided is the exception's message; consider enhancing
     * this handler to log stack traces and hide sensitive details for production environments.
     *
     * @param ex the uncaught exception
     * @param request the current HTTP request (used to obtain the request path)
     * @return a ResponseEntity with status 500 and an ApiErrorDto body describing the error
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {
        ApiErrorDto body = ApiErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}