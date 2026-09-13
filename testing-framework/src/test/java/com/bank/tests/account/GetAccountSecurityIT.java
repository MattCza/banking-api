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
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("security")
public class GetAccountSecurityIT {

    private final AccountClient accountClient = new AccountClient();


    private Long createAccountAndGetId() {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build());
        ResponseAssertions.assertStatus(response, 201);
        return response.as(AccountResponse.class).id();
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

    static Stream<Arguments> invalidTokens() {
        return Stream.of(
                Arguments.of("missing", null),
                Arguments.of("invalid", "invalid JWT"),
                Arguments.of("expired", JwtTestTokenFactory.expiredToken("admin"))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidTokens")
    @DisplayName("Should return 401 when JWT is invalid for get account")
    void shouldReturn401_WhenJwtIsInvalid(String description, String token) {
        Long accountId = createAccountAndGetId();
        Response response = accountClient.getAccountById(accountId, token);
        ErrorAssertions.assertUnauthorized(response);
    }

}