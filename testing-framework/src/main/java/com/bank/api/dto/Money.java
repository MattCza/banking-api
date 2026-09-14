package com.bank.api.dto;

import java.math.BigDecimal;

public record Money(BigDecimal amount, Currency currency) {
}
