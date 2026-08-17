package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DeleteAccountIT {

    private final AccountClient accountClient = new AccountClient();
    private final AuthClient authClient = new AuthClient();

    private void assertUnauthorized(Response response) {
        ResponseAssertions.assertStatus(response, 401);
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(errorResponse);
    }

    private Long createAccountAndGetId() {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build());
        ResponseAssertions.assertStatus(response, 201);
        return response.as(AccountResponse.class).id();
    }

    @Test
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccount_WhenAccountExists() {
        Long accountId = createAccountAndGetId();

        Response deleteResponse = accountClient.deleteAccountById(accountId);
        ResponseAssertions.assertStatus(deleteResponse, 204);

        Response getResponse = accountClient.getAccountById(accountId);
        ResponseAssertions.assertStatus(getResponse, 404);
    }

    @Test
    @DisplayName("Should return 404 when attempting to delete non-existing account")
    void shouldReturn404_WhenDeletingNonExistingAccount() {
        Response response = accountClient.deleteAccountById(999999L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }

    @Test
    @DisplayName("Should return 400 when attempting to delete account with non-numeric ID")
    void shouldReturn400_WhenDeletingNonNumericId() {
        Response response = accountClient.deleteAccountById("asd");
        ResponseAssertions.assertStatus(response, 400);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertInvalidPathParameter(errorResponse);
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
    void shouldReturn401_WhenJwtIsExpired() throws InterruptedException {
        String token = authClient.loginAndGetToken(LoginDataFactory.validAdmin());
        Long accountId = createAccountAndGetId();

        Thread.sleep(6500);
        Response secondResponse = accountClient.deleteAccountById(accountId, token);
        assertUnauthorized(secondResponse);
    }
}
