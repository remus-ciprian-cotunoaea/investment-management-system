package com.investment.orders.exception;

import com.investment.common.dto.ApiErrorDto;
import com.investment.common.exception.BusinessException;
import com.investment.common.exception.NotFoundException;
import com.investment.orders.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

/**
 * Global REST exception handler that centralizes error-to-response mapping for the orders' microservice.
 *
 * <p>This class captures common exceptions thrown by controllers and services and
 * converts them into consistent HTTP error responses using {@link ApiErrorDto}.</p>
 *
 * <p>Each handler logs the received exception (debug or error depending on severity)
 * and builds a ResponseEntity containing a suitable HTTP status, an error reason and a human-readable message.</p>
 *
 * Typical mappings:
 * - NotFoundException     -> 404 NOT FOUND
 * - BusinessException     -> 400 BAD REQUEST
 * - Validation exceptions -> 400 BAD REQUEST (with concise validation message)
 * - Message parse errors  -> 400 BAD REQUEST
 * - Illegal arguments     -> 400 BAD REQUEST
 * - Generic exceptions    -> 500 INTERNAL SERVER ERROR
 * <p>
 * The handler uses constants from {@link com.investment.orders.utils.Constants} for message templates.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle resource-not-found errors.
     *
     * <p>Logs the exception at DEBUG level and returns a 404 Not Found response
     * containing a standardized {@link ApiErrorDto} payload.</p>
     *
     * @param ex the thrown {@link NotFoundException}
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} with status 404 and an ApiErrorDto body
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.debug(Constants.MESSAGE_NOT_FOUND, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiErrorDto.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    /**
     * Handle domain/business rule violations.
     *
     * <p>BusinessException signals a client-side request that violates business rules.
     * The handler logs at DEBUG level and returns 400 Bad Request with the exception message.</p>
     *
     * @param ex the thrown {@link BusinessException}
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} with status 400 and an ApiErrorDto body
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDto> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.debug(Constants.MESSAGE_BUSINESS_ERROR, ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    /**
     * Handle validation failures for @Valid annotated controller method arguments.
     *
     * <p>Extracts a concise error message from the binding result (first field error) and
     * returns 400 Bad Request with that message to help clients correct request payloads.</p>
     *
     * @param ex the thrown {@link MethodArgumentNotValidException}
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} with status 400 and an ApiErrorDto body
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest request) {
        log.debug(Constants.MESSAGE_VALIDATION_ERROR, ex.getMessage(), ex);
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + Constants.SPACE + fe.getDefaultMessage())
                .orElse(Constants.MESSAGE_VALIDATION_FAILED);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(msg)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    /**
     * Handle numeric/constraint violations raised by jakarta. Validation on method parameters.
     *
     * <p>Builds a concise violation message from the first constraint violation and
     * returns 400 Bad Request to the client.</p>
     *
     * @param ex the thrown {@link ConstraintViolationException}
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} with status 400 and an ApiErrorDto body
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolation(ConstraintViolationException ex,
                                                                 HttpServletRequest request) {
        log.debug(Constants.MESSAGE_CONSTRAINT_VIOLATION, ex.getMessage(), ex);
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + Constants.SPACE + v.getMessage())
                .orElse(Constants.MESSAGE_CONSTRAINT);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(msg)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    /**
     * Handle HTTP message parsing errors (malformed JSON or wrong types).
     *
     * <p>Returns 400 Bad Request and includes a helpful prefix indicating a malformed payload,
     * followed by the parser's message.</p>
     *
     * @param ex the thrown {@link HttpMessageNotReadableException}
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} with status 400 and an ApiErrorDto body
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleNotReadable(HttpMessageNotReadableException ex,
                                                         HttpServletRequest request) {
        log.debug(Constants.MESSAGE_HTTP_NOT_READABLE, ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(Constants.MESSAGE_MALFORMED_FORMAT + ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    /**
     * Handle illegal argument/state exceptions thrown by application code.
     *
     * <p>Catches {@link IllegalArgumentException} and {@link IllegalStateException},
     * logs at DEBUG level and responds with 400 Bad Request and the exception message.</p>
     *
     * @param ex the thrown runtime exception (illegal argument or illegal state)
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} with status 400 and an ApiErrorDto body
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorDto> handleIllegal(RuntimeException ex, HttpServletRequest request) {
        log.debug(Constants.MESSAGE_ILLEGAL_ARGUMENT, ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(
                ApiErrorDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    /**
     * Catch-all handler for unexpected exceptions.
     *
     * <p>Logs the error at ERROR level and returns a 500 Internal Server Error with
     * the exception message. This ensures uncaught exceptions do not leak stack traces
     * to clients while still providing a consistent error payload.</p>
     *
     * @param ex the thrown {@link Exception}
     * @param request the current {@link HttpServletRequest}
     * @return a {@link ResponseEntity} with status 500 and an ApiErrorDto body
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error(Constants.MESSAGE_UNEXPECTED_ERROR, ex);
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