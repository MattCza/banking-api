package com.bank.api.dto.request;

import com.bank.api.dto.Currency;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        Currency currency
) {
}
