package com.company.orderservice.exception;

public class InvalidOrderStateTransitionException extends RuntimeException {
    public InvalidOrderStateTransitionException(String message) {
        super(message);
    }
}
