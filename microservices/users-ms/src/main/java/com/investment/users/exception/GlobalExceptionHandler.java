package com.investment.users.exception;

import com.investment.common.exception.*;
import com.investment.common.dto.*;
import com.investment.users.utils.Constants;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;


/**
 * Centralized REST exception handler that maps domain and framework exceptions
 * to standardized ApiErrorDto responses.
 *
 * <p>This controller advice intercepts exceptions thrown by controllers and
 * produces a consistent error response body (ApiErrorDto) with an appropriate
 * HTTP status code. It covers custom domain exceptions (NotFound, BadRequest,
 * Unauthorized, Forbidden, BusinessException) and common Spring/validation
 * exceptions.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle a NotFoundException and return 404 Not Found with the exception message.
     *
     * @param ex the NotFoundException thrown by the application
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 404
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, Constants.MESSAGE_NOT_FOUND, ex.getMessage());
    }

    /**
     * Handle a BadRequestException and return 400 Bad Request with the exception message.
     *
     * @param ex the BadRequestException thrown by the application
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 400
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDto> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, Constants.MESSAGE_BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handle an UnauthorizedException and return 401 Unauthorized with the exception message.
     *
     * @param ex the UnauthorizedException thrown by the application
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 401
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorDto> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, Constants.MESSAGE_UNAUTHORIZED, ex.getMessage());
    }

    /**
     * Handle a ForbiddenException and return 403 Forbidden with the exception message.
     *
     * @param ex the ForbiddenException thrown by the application
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 403
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorDto> handleForbidden(ForbiddenException ex) {
        return build(HttpStatus.FORBIDDEN, Constants.MESSAGE_FORBIDDEN, ex.getMessage());
    }

    /**
     * Handle a BusinessException and return 422 Unprocessable Entity with the business message.
     *
     * <p>Business exceptions represent domain rule violations or recoverable
     * business logic errors that should be communicated to clients with a 422 status.</p>
     *
     * @param ex the BusinessException thrown by the application
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 422
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDto> handleBusiness(BusinessException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, Constants.MESSAGE_BUSINESS_RULE, ex.getMessage());
    }

    // ==== common Spring/validation errors ====

    /**
     * Handle MethodArgumentNotValidException thrown when @Valid request body validation fails.
     *
     * <p>This implementation extracts the first field error and returns a concise
     * message in the form "field message". If no field errors are found, it
     * returns a generic 'Validation error' message.</p>
     *
     * @param ex the MethodArgumentNotValidException produced by Spring validation
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 400
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + Constants.STRING_EMPTY_SPACE + fe.getDefaultMessage())
                .orElse(Constants.MESSAGE_VALIDATION_ERROR);
        return build(HttpStatus.BAD_REQUEST, Constants.MESSAGE_BAD_REQUEST, msg);
    }

    /**
     * Handle ConstraintViolationException thrown for validation constraints outside the request body
     * (for example, @Validated method parameters).
     *
     * <p>Extracts the first constraint violation and returns a message combining
     * the property path and violation message, or a generic 'Constraint violation'
     * if none are present.</p>
     *
     * @param ex the ConstraintViolationException containing one or more violations
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 400
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + Constants.STRING_EMPTY_SPACE + v.getMessage())
                .orElse(Constants.MESSAGE_CONSTRAINT_VALIDATION);
        return build(HttpStatus.BAD_REQUEST, Constants.MESSAGE_BAD_REQUEST, msg);
    }

    /**
     * Handle HttpMessageNotReadableException which indicates malformed JSON or
     * unreadable request body payloads.
     *
     * <p>Returns HTTP 400 with a message prefixed by 'Malformed JSON request'
     * and appended with the most specific cause from the exception to aid debugging.</p>
     *
     * @param ex the HttpMessageNotReadableException produced by message conversion
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 400
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, Constants.MESSAGE_BAD_REQUEST, Constants.MESSAGE_JSON_ERROR
                + ex.getMostSpecificCause().getMessage());
    }

    /**
     * Handle DataIntegrityViolationException typically thrown by the persistence layer
     * when unique or foreign key constraints are violated.
     *
     * <p>Returns HTTP 409 Conflict and includes the root cause message to give
     * more context about the violated database constraint.</p>
     *
     * @param ex the DataIntegrityViolationException from the data layer
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 409
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> handleDataIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, Constants.DATA_INTEGRITY_ERROR, Constants.MESSAGE_KEY_CONSTRAINT
                + Objects.requireNonNull(ex.getRootCause()).getMessage());
    }

    /**
     * Catch-all handler for unexpected exceptions.
     *
     * <p>Returns HTTP 500 Internal Server Error with a generic message plus the
     * exception message. Use this to avoid leaking stack traces to clients while
     * still providing minimal diagnostic information.</p>
     *
     * @param ex the unexpected Exception
     * @return a ResponseEntity containing an ApiErrorDto with HTTP 500
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, Constants.MESSAGE_INTERNAL_ERROR, Constants.MESSAGE_UNEXPECTED_ERROR
                + ex.getMessage());
    }

    // ==== helper ====

    /**
     * Build a ResponseEntity<ApiErrorDto> populated with the provided status, error and message.
     *
     * <p>Fill the ApiErrorDto with numeric status, a short error summary and a
     * human-readable message. The path is left empty here; if desired, controller
     * code or a filter can populate request path details before returning.</p>
     *
     * @param status HTTP status to return
     * @param error short error summary (e.g. \"Bad Request\")
     * @param message detailed human-readable message for the client
     * @return ResponseEntity containing the constructed ApiErrorDto and status
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    private ResponseEntity<ApiErrorDto> build(HttpStatus status, String error, String message) {
        ApiErrorDto body = ApiErrorDto.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(Constants.STRING_EMPTY)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}