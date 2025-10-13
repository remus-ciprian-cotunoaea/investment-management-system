package com.investment.positions.exception;

import com.investment.common.exception.BadRequestException;
import com.investment.common.exception.BusinessException;
import com.investment.common.exception.ForbiddenException;
import com.investment.common.exception.NotFoundException;
import com.investment.common.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 – recurso no encontrado (desde exceptions-lib)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(simple("Not Found", ex.getMessage()));
    }

    // 400 – errores de negocio o peticiones inválidas (desde exceptions-lib)
    @ExceptionHandler({BadRequestException.class, BusinessException.class})
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(simple("Bad Request", ex.getMessage()));
    }

    // 401 – no autenticado (desde exceptions-lib)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(simple("Unauthorized", ex.getMessage()));
    }

    // 403 – sin permisos (desde exceptions-lib)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(simple("Forbidden", ex.getMessage()));
    }

    // 400 – validaciones de @RequestBody con @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("fields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 400 – validaciones de @PathVariable / @RequestParam con @Validated
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Constraint violation");
        Map<String, String> violations = new HashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            violations.put(v.getPropertyPath().toString(), v.getMessage());
        }
        body.put("violations", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 500 – fallback genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(simple("Internal Server Error", ex.getMessage()));
    }

    // --- helpers mínimos ---
    private Map<String, Object> simple(String error, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", error);
        m.put("message", message);
        return m;
    }
}