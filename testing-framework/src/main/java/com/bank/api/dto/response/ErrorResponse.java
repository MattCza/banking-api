package com.bank.api.dto.response;

import java.util.List;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, List<String>> validationErrors) {
}
