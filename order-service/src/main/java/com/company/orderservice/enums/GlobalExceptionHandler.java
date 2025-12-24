package com.company.orderservice.enums;

import com.company.orderservice.dto.ApiResponse;
import com.company.orderservice.exception.InvalidOrderStateTransitionException;
import com.company.orderservice.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderNotFound(OrderNotFoundException ex) {
        log.warn("Order not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(com.company.orderservice.exception.RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimitExceeded(com.company.orderservice.exception.RateLimitExceededException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", details);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", details);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return new ResponseEntity<>(ApiResponse.error("ROUTE_NOT_FOUND", "The requested route does not exist", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOrderStateTransitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidOrderStateTransition(InvalidOrderStateTransitionException ex) {
        log.warn("Invalid state transition: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_STATE_TRANSITION", ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, String code, String details) {
        ApiResponse.ErrorDetails errorDetails = ApiResponse.ErrorDetails.builder()
                .code(code)
                .details(details)
                .build();
        
        return new ResponseEntity<>(ApiResponse.<Void>builder()
                .success(false)
                .message("Request failed")
                .error(errorDetails)
                .build(), status);
    }
}
