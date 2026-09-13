package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.JwtTestTokenFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


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


    static Stream<Arguments> invalidTokens() {
        return Stream.of(
                Arguments.of("missing", null),
                Arguments.of("invalid", "invalid JWT"),
                Arguments.of("expired", JwtTestTokenFactory.expiredToken("admin"))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidTokens")
    @DisplayName("Should return 401 when JWT is invalid")
    void shouldReturn401_WhenJwtInvalid(String description, String token) {
        Long accountId = createAccountAndGetId();
        Response response = accountClient.deleteAccountById(accountId, token);
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
