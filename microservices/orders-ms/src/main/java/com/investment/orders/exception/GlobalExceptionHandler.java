package com.investment.orders.exception;

import com.investment.common.dto.ApiErrorDto;
import com.investment.common.exception.BusinessException;
import com.investment.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.debug("NotFoundException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiErrorDto.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDto> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.debug("BusinessException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest request) {
        log.debug("Validation error: {}", ex.getMessage(), ex);
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(msg)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolation(ConstraintViolationException ex,
                                                                 HttpServletRequest request) {
        log.debug("Constraint violation: {}", ex.getMessage(), ex);
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .orElse("Constraint violation");
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(msg)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleNotReadable(HttpMessageNotReadableException ex,
                                                         HttpServletRequest request) {
        log.debug("HttpMessageNotReadableException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("Malformed request body: " + ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorDto> handleIllegal(RuntimeException ex, HttpServletRequest request) {
        log.debug("Illegal argument/state: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiErrorDto.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }
}