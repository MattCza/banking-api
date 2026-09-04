package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.JwtTestTokenFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("security")
public class PostAccountSecurityIT {

    private final AccountClient accountClient = new AccountClient();


    private void assertUnauthorized(Response response) {
        ResponseAssertions.assertStatus(response, 401);
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(errorResponse);
    }


    @Test
    @DisplayName("Should return 401 when JWT is missing")
    void shouldReturn401_WhenJwtIsMissing() {
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();
        Response response = accountClient.createAccount(request, null);
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is invalid")
    void shouldReturn401_WhenJwtIsInvalid() {
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();
        Response response = accountClient.createAccount(request, "invalid JWT");
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 error response when attempting to create account with expired JWT")
    void shouldReturn401_WhenJwtIsExpired() throws InterruptedException {
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();
        String expiredToken = JwtTestTokenFactory.expiredToken("admin");
        Response response = accountClient.createAccount(request, expiredToken);
        assertUnauthorized(response);
    }
}
