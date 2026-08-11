package com.bank.controller;

import com.bank.dto.error.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.computeIfAbsent(fieldName, key -> new ArrayList<>()).add(errorMessage);
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
                Map.of());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "BAD_REQUEST",
                "Malformed JSON request",
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                415,
                "UNSUPPORTED_MEDIA_TYPE",
                "Unsupported Content-Type",
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "BAD_REQUEST",
                "Invalid path parameter",
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
