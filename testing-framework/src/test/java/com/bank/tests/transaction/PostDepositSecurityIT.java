package com.bank.tests.transaction;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.client.AuthClient;
import com.bank.api.client.TransactionClient;
import com.bank.api.data.JwtTestTokenFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.data.TransactionDataFactory;
import com.bank.api.dto.request.TransactionRequest;
import com.bank.tests.account.BaseAccountIT;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Tag("security")
public class PostDepositSecurityIT extends BaseAccountIT {

    private final TransactionClient transactionClient = new TransactionClient();

    static Stream<Arguments> invalidTokens() {
        return Stream.of(
                Arguments.of("missing", null),
                Arguments.of("invalid", "invalid JWT"),
                Arguments.of("expired", JwtTestTokenFactory.expiredToken("admin"))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidTokens")
    @DisplayName("Should return 401 when JWT is invalid for deposit")
    void shouldReturn401_WhenJwtIsInvalid(String description, String token) {
        Long accountId = createAccountAndGetId();
        TransactionRequest request = TransactionDataFactory.validDeposit().build();

        Response response = transactionClient.deposit(request, accountId, token);
        ErrorAssertions.assertUnauthorized(response);
    }

    @Test
    @DisplayName("Should return 403 when regular user attempts to deposit")
    void shouldReturn403_WhenRegularUserAttemptsToDeposit() {
        Long accountId = createAccountAndGetId();
        TransactionRequest request = TransactionDataFactory.validDeposit().build();
        AuthClient authClient = new AuthClient();
        String userToken = authClient.loginAndGetToken(LoginDataFactory.validUser());

        Response response = transactionClient.deposit(request, accountId, userToken);
        ErrorAssertions.assertForbidden(response);
    }
}
