package com.bookingservice.exception;
import com.bookingservice.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for REST APIs
 * Handles runtime exceptions, validation errors, and generic exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------------- Generic Runtime Exception ----------------
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse<Void>> handleRuntimeException(RuntimeException ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ---------------- Validation Exception ----------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<String>> handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));

        APIResponse<String> response = new APIResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");
        response.setData(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ---------------- Unauthorized Exception ----------------
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<APIResponse<Void>> handleSecurityException(SecurityException ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(ex.getMessage());
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // ---------------- Generic Exception ----------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGenericException(Exception ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("Internal Server Error: " + ex.getMessage());
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
