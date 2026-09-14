package com.bank.dto.transaction;

import com.bank.model.Currency;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 17, fraction = 2, message = "Amount must contain up to 17 integer digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        Currency currency
) {
}
