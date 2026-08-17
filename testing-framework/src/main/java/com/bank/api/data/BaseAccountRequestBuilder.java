package com.bank.api.data;

import net.datafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public abstract class BaseAccountRequestBuilder<T extends BaseAccountRequestBuilder<T>> {

    protected static final Faker FAKER = new Faker(Locale.forLanguageTag("en-US"));
    protected String ownerName = FAKER.name().fullName();
    protected String email = FAKER.internet().emailAddress();
    protected BigDecimal balance = randomBalance(500, 2000);

    public T withOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return (T) this;
    }
    public T withEmail(String email) {
        this.email = email;
        return (T) this;
    }
    public T withBalance(BigDecimal balance) {
        this.balance = balance;
        return (T) this;
    }

    private static BigDecimal randomBalance(long min, long max) {
        double randomDouble = FAKER.number().randomDouble(2, min, max);
        return BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);
    }
}
