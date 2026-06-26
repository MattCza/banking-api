package com.bank.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String ownerName,
        String email,
        BigDecimal balance
) {
}
