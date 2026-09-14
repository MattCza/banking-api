package com.bank.api.data;

import com.bank.api.dto.Currency;
import com.bank.api.dto.request.CreateAccountRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreateAccountRequestBuilder extends BaseAccountRequestBuilder<CreateAccountRequestBuilder> {

    private BigDecimal balance = randomBalance(500, 2000);
    private Currency currency = Currency.PLN;

    public CreateAccountRequestBuilder withBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public CreateAccountRequestBuilder withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public CreateAccountRequest build() {
        return new CreateAccountRequest(ownerName, email, balance, currency);
    }

    private static BigDecimal randomBalance(long min, long max) {
        double randomDouble = FAKER.number().randomDouble(2, min, max);
        return BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);
    }
}
