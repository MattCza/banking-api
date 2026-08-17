package com.bank.api.dto.request;

import java.math.BigDecimal;

public record UpdateAccountRequest(
        String ownerName,
        String email,
        BigDecimal balance
) {
}
