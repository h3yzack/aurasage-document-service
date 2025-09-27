package com.aurasage.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import com.aurasage.document.model.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSecurityException(SecurityException e, ServerWebExchange exchange) {
        String requestPath = exchange.getRequest().getPath().value();
        log.warn("Security violation at {}: {}", requestPath, e.getMessage());
        
        return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(IllegalArgumentException e, ServerWebExchange exchange) {
        log.error("Invalid request: {}", e.getMessage());
        
        return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception e, ServerWebExchange exchange) {
        log.error("Unexpected error: ", e);
        
        // Provide a more user-friendly message for generic exceptions
        String userMessage = "An unexpected error occurred. Please try again later.";
        return createErrorResponse(userMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a standardized error response wrapped in a Mono
     */
    private Mono<ResponseEntity<ErrorResponse>> createErrorResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(message, status.value());
        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }
}
