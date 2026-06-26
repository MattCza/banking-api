package com.bank.api.dto.response;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String ownerName,
        String email,
        BigDecimal balance
) {
}
