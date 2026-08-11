package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
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

import static org.assertj.core.api.Assertions.assertThat;

public class GetAccountIT {

    private final AccountClient accountClient = new AccountClient();
    private final AuthClient authClient = new AuthClient();

    private void assertUnauthorized(Response response) {
        ResponseAssertions.assertStatus(response, 401);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(errorResponse);
    }

    private Long createAccountAndGetId() {
        Response createResponse = accountClient.createAccount(AccountDataFactory.validAccount().build());
        ResponseAssertions.assertStatus(createResponse, 201);

        return createResponse.as(AccountResponse.class).id();
    }

    @Test
    @DisplayName("Should return account details when account exists")
    void shouldReturnAccount_WhenAccountExists() {
        CreateAccountRequest request = AccountDataFactory.validAccount().build();

        Response response = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(response, 201);
        AccountResponse createdAccount = response.as(AccountResponse.class);

        Response getResponse = accountClient.getAccountById(createdAccount.id());
        ResponseAssertions.assertStatus(getResponse, 200);
        AccountResponse fetchedAccount = getResponse.as(AccountResponse.class);

        assertThat(fetchedAccount.id()).isEqualTo(createdAccount.id());
        AccountAssertions.assertCreatedAccountResponse(fetchedAccount, request);
    }

    @Test
    @DisplayName("Should return 404 error response when account does not exist")
    void shouldReturn404_WhenAccountDoesNotExist() {
        Response response = accountClient.getAccountById(999999L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }

    @Test
    @DisplayName("Should return 404 error response when account ID is 0")
    void shouldReturn404_WhenAccountIdIsZero() {
        Response response = accountClient.getAccountById(0L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }

    @Test
    @DisplayName("Should return 404 error response when account ID is -1")
    void shouldReturn404_WhenAccountIdIsNegative() {
        Response response = accountClient.getAccountById(-1L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }

    // Error handling in BE -> DONE
    @Test
    @DisplayName("Should return 400 when account ID is not a number")
    void shouldReturn400_WhenAccountIdIsNotANumber() {
        Response response = accountClient.getAccountById("asd");
        ResponseAssertions.assertStatus(response, 400);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertInvalidPathParameter(errorResponse);
    }

    @Test
    @DisplayName("Should return 401 when JWT is missing for get account")
    void shouldReturn401_WhenJwtIsMissing() {
        Long accountId = createAccountAndGetId();
        Response response = accountClient.getAccountById(accountId, null);
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is invalid for get account")
    void shouldReturn401_WhenJwtIsInvalid() {
        Long accountId = createAccountAndGetId();
        Response response = accountClient.getAccountById(accountId, "invalid JWT");
        assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 401 when JWT is expired for get account")
    void shouldReturn401_WhenJwtIsExpired() throws InterruptedException {
        LoginRequest loginRequest = LoginDataFactory.validAdmin();
        String token = authClient.loginAndGetToken(loginRequest);

        Long accountId = createAccountAndGetId();

        Response firstResponse = accountClient.getAccountById(accountId, token);
        ResponseAssertions.assertStatus(firstResponse, 200);

        Thread.sleep(6500);
        Response secondResponse = accountClient.getAccountById(accountId, token);

        assertUnauthorized(secondResponse);
    }
}
