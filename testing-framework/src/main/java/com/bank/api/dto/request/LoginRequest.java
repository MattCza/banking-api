package com.bank.api.dto.request;

public record LoginRequest(
        String username,
        String password) {
}
