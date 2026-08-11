package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.request.LoginRequest;
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

    @Test
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccount_WhenAccountExists() {
        CreateAccountRequest request = AccountDataFactory.validAccount().build();

        Response response = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(response, 201);

        AccountResponse createdAccount = response.as(AccountResponse.class);
        Response deleteResponse = accountClient.deleteAccountById(createdAccount.id());
        ResponseAssertions.assertStatus(deleteResponse, 204);

        Response getResponse = accountClient.getAccountById(createdAccount.id());
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
    @DisplayName("Should return 401 when JWT is missing for delete account")
    void shouldReturn401_WhenJwtIsMissing() {
        Response createResponse = accountClient.createAccount(AccountDataFactory.validAccount().build());
        ResponseAssertions.assertStatus(createResponse, 201);

        Long accountId = createResponse.as(AccountResponse.class).id();
        Response response = accountClient.deleteAccountById(accountId, null);

        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is invalid for delete account")
    void shouldReturn401_WhenJwtIsInvalid() {
        Response createResponse = accountClient.createAccount(AccountDataFactory.validAccount().build());
        ResponseAssertions.assertStatus(createResponse, 201);

        Long accountId = createResponse.as(AccountResponse.class).id();
        Response response = accountClient.deleteAccountById(accountId, "invalid JWT");

        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is expired for delete account")
    void shouldReturn401_WhenJwtIsExpired() throws InterruptedException {
        LoginRequest loginRequest = LoginDataFactory.validAdmin();
        String token = authClient.loginAndGetToken(loginRequest);

        Response createResponse = accountClient.createAccount(AccountDataFactory.validAccount().build());
        ResponseAssertions.assertStatus(createResponse, 201);

        Long accountId = createResponse.as(AccountResponse.class).id();
        Response firstResponse = accountClient.deleteAccountById(accountId, token);
        ResponseAssertions.assertStatus(firstResponse, 204);

        Response secondCreateResponse = accountClient.createAccount(AccountDataFactory.validAccount().build());
        ResponseAssertions.assertStatus(secondCreateResponse, 201);

        Thread.sleep(6500);
        Long secondAccountId = secondCreateResponse.as(AccountResponse.class).id();
        Response secondResponse = accountClient.deleteAccountById(secondAccountId, token);

        assertUnauthorized(secondResponse);
    }
}
