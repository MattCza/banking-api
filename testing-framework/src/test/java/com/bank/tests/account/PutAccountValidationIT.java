package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.ValidationAssertions;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.request.UpdateAccountRequest;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Tag("validation")
public class PutAccountValidationIT extends BaseAccountIT {

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
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName(ownerName).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);
            ResponseAssertions.assertMatchesSchema(updateResponse, "schemas/error-response-schema.json");

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", expectedMessages);
        }

        @Test
        @DisplayName("Should return 200 and update account when owner name has min length - 3 chars")
        void shouldUpdateAccount_WhenOwnerNameHasMinLength() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName("Max").build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 200);

            AccountResponse accountResponse = updateResponse.as(AccountResponse.class);
            AccountAssertions.assertUpdatedAccountResponse(accountResponse, updateAccountRequest, id);
        }

        @Test
        @DisplayName("Should return 200 and update account when owner name has max length - 50 chars")
        void shouldUpdateAccount_WhenOwnerNameHasMaxLength() {
            Long id = createAccountAndGetId();

            String ownerName = "A".repeat(50);
            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName(ownerName).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 200);

            AccountResponse accountResponse = updateResponse.as(AccountResponse.class);
            AccountAssertions.assertUpdatedAccountResponse(accountResponse, updateAccountRequest, id);
        }
    }

    @Nested
    @DisplayName("Email validation")
    class EmailValidation {

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
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withEmail(email).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);
            ResponseAssertions.assertMatchesSchema(updateResponse, "schemas/error-response-schema.json");

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", expectedMessages);
        }

        @Test
        @DisplayName("Should return 200 and update account when email has max length - 254 chars")
        void shouldUpdateAccount_WhenEmailHasMaxLength() {
            Long id = createAccountAndGetId();

            String email = AccountDataFactory.emailWithLength(254);
            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withEmail(email).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 200);

            AccountResponse accountResponse = updateResponse.as(AccountResponse.class);
            AccountAssertions.assertUpdatedAccountResponse(accountResponse, updateAccountRequest, id);
        }
    }

}