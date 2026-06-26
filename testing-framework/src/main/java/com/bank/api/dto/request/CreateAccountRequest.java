package com.bank.api.dto.request;

import java.math.BigDecimal;

public record CreateAccountRequest(
        String ownerName,
        String email,
        BigDecimal initialBalance
) {
}
