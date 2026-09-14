package com.bank.api.data;

import net.datafaker.Faker;

import java.util.Locale;

public abstract class BaseAccountRequestBuilder<T extends BaseAccountRequestBuilder<T>> {

    protected static final Faker FAKER = new Faker(Locale.forLanguageTag("en-US"));
    protected String ownerName = FAKER.name().fullName();
    protected String email = FAKER.internet().emailAddress();

    public T withOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return (T) this;
    }
    public T withEmail(String email) {
        this.email = email;
        return (T) this;
    }
}
