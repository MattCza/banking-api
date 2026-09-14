package com.bank.dto.transaction;

import com.bank.model.Money;
import com.bank.model.TransactionType;

import java.time.Instant;

public record TransactionResponse(
        Long id,
        Long accountId,
        Money money,
        TransactionType type,
        Instant createdAt
) {
}
