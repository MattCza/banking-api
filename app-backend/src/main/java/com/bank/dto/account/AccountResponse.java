package com.bank.dto.account;

import com.bank.model.Money;

public record AccountResponse(
        Long id,
        String ownerName,
        String email,
        Money money
) {
}
