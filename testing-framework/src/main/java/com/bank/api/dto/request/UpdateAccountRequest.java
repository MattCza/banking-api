package com.bank.api.dto.request;

public record UpdateAccountRequest(
        String ownerName,
        String email
) {
}
