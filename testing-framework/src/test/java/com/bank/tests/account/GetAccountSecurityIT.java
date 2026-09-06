package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("security")
public class GetAccountSecurityIT {

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
    @DisplayName("Should return 200 when regular user attempts to get an account")
    public void shouldReturn200_WhenRegularUserAttemptsToGetAccount() {
        CreateAccountRequest createAccountRequest = AccountDataFactory.validCreateAccount().build();
        Response createAccountResponse = accountClient.createAccount(createAccountRequest);
        ResponseAssertions.assertStatus(createAccountResponse, 201);
        Long accountId = createAccountResponse.as(AccountResponse.class).id();


        AuthClient authClient = new AuthClient();
        String userToken = authClient.loginAndGetToken(LoginDataFactory.validUser());
        Response getAccountResponse = accountClient.getAccountById(accountId, userToken);

        ResponseAssertions.assertJsonResponse(getAccountResponse, 200);
        AccountResponse fetchedAccount = getAccountResponse.as(AccountResponse.class);

        assertThat(fetchedAccount.id()).isEqualTo(accountId);
        AccountAssertions.assertCreatedAccountResponse(fetchedAccount, createAccountRequest);
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
    void shouldReturn401_WhenJwtIsExpired() {
        String expiredToken = JwtTestTokenFactory.expiredToken("admin");
        Long accountId = createAccountAndGetId();
        Response response = accountClient.getAccountById(accountId, expiredToken);
        assertUnauthorized(response);
    }
}
