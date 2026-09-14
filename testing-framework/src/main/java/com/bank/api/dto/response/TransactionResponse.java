package com.bank.api.dto.response;

import com.bank.api.dto.Money;
import com.bank.api.dto.TransactionType;

import java.time.Instant;

public record TransactionResponse(
        Long id,
        Long accountId,
        Money money,
        TransactionType type,
        Instant createdAt
) {
}
