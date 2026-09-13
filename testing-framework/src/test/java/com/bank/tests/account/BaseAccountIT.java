package com.bank.tests.account;

import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.response.AccountResponse;
import io.restassured.response.Response;

abstract class BaseAccountIT {

    protected final AccountClient accountClient = new AccountClient();

    protected Long createAccountAndGetId() {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build());
        ResponseAssertions.assertStatus(response, 201);
        return response.as(AccountResponse.class).id();
    }
}
