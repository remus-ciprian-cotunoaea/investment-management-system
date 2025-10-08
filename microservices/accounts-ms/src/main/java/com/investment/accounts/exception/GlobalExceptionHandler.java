package com.investment.accounts.exception;

import com.investment.common.dto.ApiErrorDto;
import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.BusinessException;
import com.investment.common.exception.ForbiddenException;
import com.investment.common.exception.NotFoundException;
import com.investment.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // Excepciones de tu librería
    // =========================
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.debug("NotFound: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDto> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        log.debug("BadRequest: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorDto> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        log.debug("Unauthorized: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorDto> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        log.debug("Forbidden: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    // Negocio: 422 (puedes cambiar a 409 o 400 si prefieres)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDto> handleBusiness(BusinessException ex, HttpServletRequest req) {
        log.debug("BusinessException: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    // =========================
    // Spring / Validaciones
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest req) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        log.debug("Validation (body) error(s): {}", details);
        return build(HttpStatus.BAD_REQUEST, details, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolation(ConstraintViolationException ex,
                                                                 HttpServletRequest req) {
        String details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        log.debug("Validation (params) error(s): {}", details);
        return build(HttpStatus.BAD_REQUEST, details, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        log.debug("Malformed JSON: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = "Data integrity violation";
        ex.getMostSpecificCause();
        String cause = ex.getMostSpecificCause().getMessage();
        log.warn("{} at {} -> {}", msg, req.getRequestURI(), cause);
        return build(HttpStatus.CONFLICT, msg, req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.debug("IllegalArgument: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    // =========================
    // Fallback
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error at {}:", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req);
    }

    // =========================
    // Helpers
    // =========================
    private ResponseEntity<ApiErrorDto> build(HttpStatus status, String message, HttpServletRequest req) {
        ApiErrorDto body = ApiErrorDto.builder()
                .timestamp(OffsetDateTime.now().toLocalDateTime())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }

    private String formatFieldError(FieldError fe) {
        String msg = fe.getDefaultMessage();
        if (msg == null || msg.isBlank()) msg = "invalid value";
        return fe.getField() + ": " + msg;
    }
}