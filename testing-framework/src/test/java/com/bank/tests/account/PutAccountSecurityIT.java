package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.JwtTestTokenFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.request.UpdateAccountRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Tag("security")
public class PutAccountSecurityIT extends BaseAccountIT {

    static Stream<Arguments> invalidTokens() {
        return Stream.of(
                Arguments.of("missing", null),
                Arguments.of("invalid", "invalid JWT"),
                Arguments.of("expired", JwtTestTokenFactory.expiredToken("admin"))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidTokens")
    @DisplayName("Should return 401 when JWT is invalid for update account")
    void shouldReturn401_WhenJwtIsInvalid(String description, String token) {
        Long id = createAccountAndGetId();
        UpdateAccountRequest request = AccountDataFactory.validUpdateAccount().build();
        Response response = accountClient.updateAccount(request, id, token);
        ErrorAssertions.assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 403 when regular user attempts to update an account")
    public void shouldReturn403_WhenRegularUserAttemptsToUpdateAccount() {
        Long id = createAccountAndGetId();
        UpdateAccountRequest request = AccountDataFactory.validUpdateAccount().build();
        AuthClient authClient = new AuthClient();
        String userToken = authClient.loginAndGetToken(LoginDataFactory.validUser());
        Response response = accountClient.updateAccount(request, id, userToken);

        ErrorAssertions.assertForbidden(response);
    }

}