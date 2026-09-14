package com.bank.model;

import com.bank.exception.CurrencyMismatchException;

import java.math.BigDecimal;

public record Money(BigDecimal amount, Currency currency) {

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    private void requireSameCurrency(Money other) {
        if (currency != other.currency) {
            throw new CurrencyMismatchException(
                    "Currency mismatch: account is in " + currency + " but operation was in " + other.currency);
        }
    }
}
