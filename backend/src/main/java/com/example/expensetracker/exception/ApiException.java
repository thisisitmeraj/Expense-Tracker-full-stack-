package com.example.expensetracker.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception that carries the HTTP status we want the client to see.
 * GlobalExceptionHandler turns this into a JSON body.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
