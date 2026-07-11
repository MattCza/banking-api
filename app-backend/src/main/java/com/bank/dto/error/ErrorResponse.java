package com.bank.dto.error;

import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> validationErrors) {

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null);
    }
}