package com.bank.api.dto.response;

import com.bank.api.dto.Money;

public record AccountResponse(
        Long id,
        String ownerName,
        String email,
        Money money
) {
}
