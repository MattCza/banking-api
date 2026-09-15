package com.bank.tests.transaction;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.ValidationAssertions;
import com.bank.api.client.TransactionClient;
import com.bank.api.data.TransactionDataFactory;
import com.bank.api.dto.Currency;
import com.bank.api.dto.request.TransactionRequest;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import com.bank.tests.account.BaseAccountIT;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("validation")
public class PostDepositValidationIT extends BaseAccountIT {

    private final TransactionClient transactionClient = new TransactionClient();

    static Stream<Arguments> invalidAmounts() {
        return Stream.of(
                Arguments.of("null", null, new String[]{"Amount cannot be null"}),
                Arguments.of("zero", BigDecimal.ZERO, new String[]{"Amount must be greater than zero"}),
                Arguments.of("negative", new BigDecimal("-10.00"), new String[]{"Amount must be greater than zero"}),
                Arguments.of("more than 2 decimal places", new BigDecimal("10.123"),
                        new String[]{"Amount must contain up to 17 integer digits and 2 decimal places"}),
                Arguments.of("more than 17 integer digits", new BigDecimal("123456789012345678.12"),
                        new String[]{"Amount must contain up to 17 integer digits and 2 decimal places"})
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidAmounts")
    @DisplayName("Should return 400 when deposit amount is invalid")
    void shouldReturn400_WhenAmountIsInvalid(String description, BigDecimal amount, String[] expectedMessages) {
        Long accountId = createAccountAndGetId();
        TransactionRequest request = TransactionDataFactory.validDeposit().withAmount(amount).build();

        Response response = transactionClient.deposit(request, accountId);
        ResponseAssertions.assertStatus(response, 400);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(errorResponse);
        ValidationAssertions.assertFieldErrors(errorResponse, "amount", expectedMessages);
    }

    @Test
    @DisplayName("Should return 400 when currency is null")
    void shouldReturn400_WhenCurrencyIsNull() {
        Long accountId = createAccountAndGetId();
        TransactionRequest request = TransactionDataFactory.validDeposit().withCurrency(null).build();

        Response response = transactionClient.deposit(request, accountId);
        ResponseAssertions.assertStatus(response, 400);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(errorResponse);
        ValidationAssertions.assertFieldErrors(errorResponse, "currency", "Currency is required");
    }

    @Test
    @DisplayName("Should return 409 when deposit currency differs from the account's currency")
    void shouldReturn409_WhenCurrencyDoesNotMatchAccount() {
        Long accountId = createAccountAndGetId();
        BigDecimal balanceBefore = currentBalance(accountId);

        TransactionRequest request = TransactionDataFactory.validDeposit().withCurrency(Currency.USD).build();

        Response response = transactionClient.deposit(request, accountId);
        ResponseAssertions.assertStatus(response, 409);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertCurrencyMismatch(errorResponse);

        assertThat(currentBalance(accountId))
                .as("a failed deposit must not mutate the account balance")
                .isEqualByComparingTo(balanceBefore);
    }

    private BigDecimal currentBalance(Long accountId) {
        return accountClient.getAccountById(accountId).as(AccountResponse.class).money().amount();
    }
}
