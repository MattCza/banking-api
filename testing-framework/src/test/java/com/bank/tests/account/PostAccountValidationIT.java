package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.ValidationAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.Currency;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

@Tag("validation")
class PostAccountValidationIT {

    AccountClient accountClient = new AccountClient();

    @Nested
    @DisplayName("Owner name validation")
    class OwnerNameValidation {

        static Stream<Arguments> invalidOwnerNames() {
            return Stream.of(
                    Arguments.of("null", null, new String[]{"Owner name must not be blank"}),
                    Arguments.of("blank", "", new String[]{"Owner name must not be blank", "Owner name must be between 3 and 50 characters"}),
                    Arguments.of("only whitespace", "     ", new String[]{"Owner name must not be blank"}),
                    Arguments.of("too short", "JL", new String[]{"Owner name must be between 3 and 50 characters"}),
                    Arguments.of("too long", "A".repeat(51), new String[]{"Owner name must be between 3 and 50 characters"})
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidOwnerNames")
        @DisplayName("Should return 400 when owner name is invalid")
        void shouldReturn400_WhenOwnerNameIsInvalid(String description, String ownerName, String[] expectedMessages) {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName(ownerName).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);
            ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", expectedMessages);
        }

        @Test
        @DisplayName("Should return 201 and create account when owner name has min length - 3 chars")
        void shouldCreateAccount_WhenOwnerNameHasMinLength() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName("Max").build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }

        @Test
        @DisplayName("Should return 201 and create account when owner name has max length - 50 chars")
        void shouldCreateAccount_WhenOwnerNameHasMaxLength() {
            String ownerName = "A".repeat(50);
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName(ownerName).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }


    }

    @Nested
    @DisplayName("Email validation")
    class EmailValidation  {

        static Stream<Arguments> invalidEmails() {
            return Stream.of(
                    Arguments.of("null", null, new String[]{"Email cannot be empty"}),
                    Arguments.of("blank", "", new String[]{"Email cannot be empty"}),
                    Arguments.of("leading whitespace", "     @gmail.com", new String[]{"Email must be a valid format"}),
                    Arguments.of("invalid format", "MCSWT", new String[]{"Email must be a valid format"}),
                    Arguments.of("exceeds max length - 255 chars", AccountDataFactory.emailWithLength(255),
                            new String[]{"Email must be at most 254 characters long"})
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidEmails")
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400_WhenEmailIsInvalid(String description, String email, String[] expectedMessages) {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail(email).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", expectedMessages);
        }

        static Stream<Arguments> validEmails() {
            return Stream.of(
                    Arguments.of("valid format", "john.doe@example.com"),
                    Arguments.of("max length - 254 chars", AccountDataFactory.emailWithLength(254))
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("validEmails")
        @DisplayName("Should create account when email is valid")
        void shouldCreateAccount_WhenEmailIsValid(String description, String email) {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail(email).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }
    }

    @Nested
    @DisplayName("Initial Balance validation")
    class BalanceValidation {

        static Stream<Arguments> invalidBalances() {
            return Stream.of(
                    Arguments.of("null", null, new String[]{"Initial balance cannot be null"}),
                    Arguments.of("negative", new BigDecimal("-1"), new String[]{"Initial balance cannot be negative"}),
                    Arguments.of("more than 2 decimal places", new BigDecimal("100.123"),
                            new String[]{"Initial balance must contain up to 17 integer digits and 2 decimal places"}),
                    Arguments.of("more than 17 integer digits", new BigDecimal("123456789012345678.12"),
                            new String[]{"Initial balance must contain up to 17 integer digits and 2 decimal places"})
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidBalances")
        @DisplayName("Should return 400 when initial balance is invalid")
        void shouldReturn400_WhenInitialBalanceIsInvalid(String description, BigDecimal balance, String[] expectedMessages) {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(balance).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "initialBalance", expectedMessages);
        }

        static Stream<Arguments> validBalances() {
            return Stream.of(
                    Arguments.of("zero", BigDecimal.ZERO),
                    Arguments.of("maximum allowed precision", new BigDecimal("12345678901234567.12"))
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("validBalances")
        @DisplayName("Should create account when initial balance is valid")
        void shouldCreateAccount_WhenInitialBalanceIsValid(String description, BigDecimal balance) {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(balance).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }
    }

    @Nested
    @DisplayName("Currency validation")
    class CurrencyValidation {

        @Test
        @DisplayName("Should return 400 when currency is null")
        void shouldReturn400_WhenCurrencyIsNull() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withCurrency(null).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);
            ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "currency", "Currency is required");
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @EnumSource(Currency.class)
        @DisplayName("Should create account for every supported currency")
        void shouldCreateAccount_WhenCurrencyIsSupported(Currency currency) {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withCurrency(currency).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }
    }

}