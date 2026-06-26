package com.bank.controller;

import com.bank.dto.error.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "BAD_REQUEST",
                "Validation failed",
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(com.bank.exception.DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(com.bank.exception.DuplicateEmailException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                409,
                "CONFLICT",
                ex.getMessage(),
                Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(com.bank.exception.AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(com.bank.exception.AccountNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "NOT_FOUND",
                ex.getMessage(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    }
}
