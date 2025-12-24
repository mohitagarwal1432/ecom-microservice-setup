package com.company.inventoryservice.exception;

import com.company.inventoryservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientInventory(InsufficientInventoryException ex) {
        return new ResponseEntity<>(ApiResponse.error("INVENTORY_INSUFFICIENT", ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductNotFound(ProductNotFoundException ex) {
        return new ResponseEntity<>(ApiResponse.error("PRODUCT_NOT_FOUND", ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return new ResponseEntity<>(ApiResponse.error("VALIDATION_FAILED", "Validation failed", errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return new ResponseEntity<>(ApiResponse.error("ROUTE_NOT_FOUND", "The requested route does not exist", ex.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
