package com.bank.api.data;

import com.bank.api.dto.Currency;
import com.bank.api.dto.request.TransactionRequest;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class TransactionRequestBuilder {

    private static final Faker FAKER = new Faker(Locale.forLanguageTag("en-US"));

    private BigDecimal amount = randomAmount(10, 200);
    private Currency currency = Currency.PLN;

    public TransactionRequestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionRequestBuilder withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public TransactionRequest build() {
        return new TransactionRequest(amount, currency);
    }

    private static BigDecimal randomAmount(long min, long max) {
        double randomDouble = FAKER.number().randomDouble(2, min, max);
        return BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);
    }
}
