package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.JwtTestTokenFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("security")
public class DeleteAccountSecurityIT {

    private final AccountClient accountClient = new AccountClient();


    private Long createAccountAndGetId() {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build());
        ResponseAssertions.assertStatus(response, 201);
        return response.as(AccountResponse.class).id();
    }

    private void assertUnauthorized(Response response) {
        ResponseAssertions.assertStatus(response, 401);
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(errorResponse);
    }


    @Test
    @DisplayName("Should return 401 when JWT is missing for delete account")
    void shouldReturn401_WhenJwtIsMissing() {
        Long accountId = createAccountAndGetId();
        Response response = accountClient.deleteAccountById(accountId, null);
        response.then().log().all();
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is invalid for delete account")
    void shouldReturn401_WhenJwtIsInvalid() {
        Long accountId = createAccountAndGetId();
        Response response = accountClient.deleteAccountById(accountId, "invalid JWT");
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is expired for delete account")
    void shouldReturn401_WhenJwtIsExpired() {
        String expiredToken = JwtTestTokenFactory.expiredToken("admin");
        Long accountId = createAccountAndGetId();
        Response response = accountClient.deleteAccountById(accountId, expiredToken);
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 403 when regular user attempts to delete an account")
    public void shouldReturn403_WhenRegularUserAttemptsToDeleteAccount() {
        Long accountId = createAccountAndGetId();
        AuthClient authClient = new AuthClient();
        String userToken = authClient.loginAndGetToken(LoginDataFactory.validUser());
        Response response = accountClient.deleteAccountById(accountId, userToken);

        ResponseAssertions.assertJsonResponse(response, 403);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertForbidden(error);
    }

}
