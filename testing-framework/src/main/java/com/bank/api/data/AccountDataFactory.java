package com.bank.api.data;

public final class AccountDataFactory {
    private AccountDataFactory() {
    }

    public static CreateAccountRequestBuilder validAccount() {
        return new CreateAccountRequestBuilder();
    }
}
