package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.JwtTestTokenFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.request.LoginRequest;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AccountSecurityIT {

    private final AccountClient accountClient = new AccountClient();
    private final AuthClient authClient = new AuthClient();

    private void assertUnauthorized(Response response) {
        ResponseAssertions.assertStatus(response, 401);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(errorResponse);
    }

    @Test
    @DisplayName("Should return 401 when JWT is missing")
    void shouldReturn401_WhenJwtIsMissing() {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build(), null);
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is invalid")
    void shouldReturn401_WhenJwtIsInvalid() {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build(), "invalid JWT");
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 error response when attempting to create account with expired JWT")
    void shouldReturn401_WhenJwtIsExpired() throws InterruptedException {
        String expiredToken = JwtTestTokenFactory.expiredToken("admin");
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build(), expiredToken);
        assertUnauthorized(response);
    }
}
