package com.bank.api.data;

import com.bank.api.dto.request.CreateAccountRequest;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class CreateAccountRequestBuilder {

    private static final Faker FAKER = new Faker(Locale.forLanguageTag("en-US"));

    private String ownerName = FAKER.name().fullName();

    private String email = FAKER.internet().emailAddress();

    private BigDecimal balance = randomBalance(500, 2000);

    public CreateAccountRequestBuilder withOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public CreateAccountRequestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public CreateAccountRequestBuilder withBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public CreateAccountRequest build() {
        return new CreateAccountRequest(ownerName, email, balance);
    }

    private static BigDecimal randomBalance(double min, double max) {

        double randomDouble = FAKER.number().randomDouble(2, (long) min, (long) max);

        return BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);
    }
}
