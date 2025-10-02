package com.investment.portfolios.exception;

import com.investment.common.dto.ApiErrorDto;
import com.investment.common.exception.BusinessException;
import com.investment.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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