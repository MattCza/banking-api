package com.bank.dto.account;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String ownerName,
        String email,
        BigDecimal balance
) {
}
