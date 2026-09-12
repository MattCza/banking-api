package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.ValidationAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@Tag("validation")
class PostAccountValidationIT {

    AccountClient accountClient = new AccountClient();

    @Nested
    @DisplayName("Owner name validation")
    class OwnerNameValidation {

        @Test
        @DisplayName("Should return 400 when owner name is null")
        void shouldReturn400_WhenOwnerNameIsNull() {
            // Arrange
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName(null).build();

            // Act
            Response response = accountClient.createAccount(request);

            // Assert
            ResponseAssertions.assertStatus(response, 400);
            ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must not be blank");
        }

        @Test
        @DisplayName("Should return 400 when owner name is blank")
        void shouldReturn400_WhenOwnerNameIsBlank() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName("").build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName",
                    "Owner name must not be blank",
                    "Owner name must be between 3 and 50 characters");
        }

        @Test
        @DisplayName("Should return 400 when owner name contains only whitespaces")
        void shouldReturn400_WhenOwnerNameContainsOnlyWhitespaces() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName("     ").build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must not be blank");
        }

        @Test
        @DisplayName("Should return 400 when owner name is too short")
        void shouldReturn400_WhenOwnerNameIsTooShort() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName("JL").build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must be between 3 and 50 characters");
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

        @Test
        @DisplayName("Should return 400 when owner name exceeds max length")
        void shouldReturn400_WhenOwnerNameExceedsMaxLength() {
            String ownerName = "A".repeat(51);
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withOwnerName(ownerName).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must be between 3 and 50 characters");
        }

    }

    @Nested
    @DisplayName("Email validation")
    class EmailValidation  {

        @Test
        @DisplayName("Should return 400 when email is null")
        void shouldReturn400_WhenEmailIsNull() {
            // Arrange
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail(null).build();

            // Act
            Response response = accountClient.createAccount(request);

            // Assert
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email cannot be empty");
        }

        @Test
        @DisplayName("Should return 400 when email is empty")
        void shouldReturn400_WhenEmailIsBlank() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail("").build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email cannot be empty");
        }

        @Test
        @DisplayName("Should return 400 when email contains whitespaces")
        void shouldReturn400_WhenEmailContainsLeadingWhitespaces() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail("     @gmail.com").build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email must be a valid format");
        }

        @Test
        @DisplayName("Should return 400 when email has invalid format")
        void shouldReturn400_WhenEmailIsInvalidFormated() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail("MCSWT").build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email must be a valid format");
        }

        @Test
        @DisplayName("Should return 201 and create account when email is valid format")
        void shouldCreateAccount_WhenEmailIsValid() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }

        @Test
        @DisplayName("Should return 201 and create account when email has max length - 254 chars")
        void shouldCreateAccount_WhenEmailHasMaxLength() {
            String email = AccountDataFactory.emailWithLength(254);
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail(email).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }

        @Test
        @DisplayName("Should return 400 when email exceeds max length - 255 chars")
        void shouldReturn400_WhenEmailExceedsMaxLength() {
            String email = AccountDataFactory.emailWithLength(255);
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withEmail(email).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email must be at most 254 characters long");
        }
    }

    @Nested
    @DisplayName("Initial Balance validation")
    class BalanceValidation {

        @Test
        @DisplayName("Should return 400 when initial balance is null")
        void shouldReturn400_WhenInitialBalanceIsNull() {
            // Arrange
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(null).build();

            // Act
            Response response = accountClient.createAccount(request);

            // Assert
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "initialBalance", "Initial balance cannot be null");
        }

        @Test
        @DisplayName("Should return 400 when initial balance is negative value")
        void shouldReturn400_WhenInitialBalanceIsNegative() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(new BigDecimal("-1")).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "initialBalance", "Initial balance cannot be negative");
        }

        @Test
        @DisplayName("Should return 201 when initial balance is 0")
        void shouldReturn201_WhenInitialBalanceIsZero() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(BigDecimal.ZERO).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }

        @Test
        @DisplayName("Should return 201 when initial balance contains the maximum allowed number of integer and fraction digits")
        void shouldReturn201_WhenInitialBalanceHasMaximumAllowedPrecision() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(new BigDecimal("12345678901234567.12")).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 201);

            AccountResponse accountResponse = response.as(AccountResponse.class);
            AccountAssertions.assertCreatedAccountResponse(accountResponse, request);
        }

        @Test
        @DisplayName("Should return 400 when initial balance contains more than 2 decimal places")
        void shouldReturn400_WhenInitialBalanceContainsMoreThan2FractionDigits() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(new BigDecimal("100.123")).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "initialBalance", "Initial balance must contain up to 17 integer digits and 2 decimal places");
        }

        @Test
        @DisplayName("Should return 400 when initial balance contains more than 17 integer digits")
        void shouldReturn400_WhenInitialBalanceContainsMoreThan17IntegerDigits() {
            CreateAccountRequest request = AccountDataFactory.validCreateAccount().withBalance(new BigDecimal("123456789012345678.12")).build();

            Response response = accountClient.createAccount(request);
            ResponseAssertions.assertStatus(response, 400);

            ErrorResponse errorResponse = response.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "initialBalance", "Initial balance must contain up to 17 integer digits and 2 decimal places");
        }


    }













}
