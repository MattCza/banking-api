package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.ValidationAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.request.UpdateAccountRequest;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PutAccountValidationIT {

    AccountClient accountClient = new AccountClient();

    private Long createAccountAndGetId() {
        CreateAccountRequest createAccountRequest = AccountDataFactory.validCreateAccount().build();
        Response response = accountClient.createAccount(createAccountRequest);
        Assertions.assertEquals(201, response.getStatusCode());
        return response.as(AccountResponse.class).id();
    }

    @Nested
    @DisplayName("Owner name validation")
    class OwnerNameValidation {

        @Test
        @DisplayName("Should return 400 when owner name is null")
        void shouldReturn400_WhenOwnerNameIsNull() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName(null).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must not be blank");
        }

        @Test
        @DisplayName("Should return 400 when owner name is blank")
        void shouldReturn400_WhenOwnerNameIsBlank() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName("").build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must not be blank",
                    "Owner name must be between 3 and 50 characters");
        }

        @Test
        @DisplayName("Should return 400 when owner name contains only whitespaces")
        void shouldReturn400_WhenOwnerNameContainsOnlyWhitespaces() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName("     ").build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must not be blank");
        }

        @Test
        @DisplayName("Should return 400 when owner name is too short")
        void shouldReturn400_WhenOwnerNameIsTooShort() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName("JL").build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must be between 3 and 50 characters");
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

        @Test
        @DisplayName("Should return 400 when owner name exceeds max length")
        void shouldReturn400_WhenOwnerNameExceedsMaxLength() {
            Long id = createAccountAndGetId();

            String ownerName = "A".repeat(51);
            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withOwnerName(ownerName).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "ownerName", "Owner name must be between 3 and 50 characters");
        }
    }

    @Nested
    @DisplayName("Email validation")
    class EmailValidation {

        @Test
        @DisplayName("Should return 400 when email is null")
        void shouldReturn400_WhenEmailIsNull() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withEmail(null).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email cannot be empty");
        }

        @Test
        @DisplayName("Should return 400 when email is empty")
        void shouldReturn400_WhenEmailIsBlank() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withEmail("").build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email cannot be empty");
        }

        @Test
        @DisplayName("Should return 400 when email contains whitespaces")
        void shouldReturn400_WhenEmailContainsLeadingWhitespaces() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withEmail("     @gmail.com").build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email must be a valid format");
        }

        @Test
        @DisplayName("Should return 400 when email has invalid format")
        void shouldReturn400_WhenEmailIsInvalidFormated() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withEmail("MCSWT").build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email must be a valid format");
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

        @Test
        @DisplayName("Should return 400 when email exceeds max length - 255 chars")
        void shouldReturn400_WhenEmailExceedsMaxLength() {
            Long id = createAccountAndGetId();

            String email = AccountDataFactory.emailWithLength(255);
            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withEmail(email).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "email", "Email must be at most 254 characters long");
        }
    }

    @Nested
    @DisplayName("Balance validation")
    class BalanceValidation {

        @Test
        @DisplayName("Should return 400 when balance is null")
        void shouldReturn400_WhenBalanceIsNull() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withBalance(null).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "balance", "Initial balance cannot be null");
        }

        @Test
        @DisplayName("Should return 400 when balance is negative value")
        void shouldReturn400_WhenBalanceIsNegative() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withBalance(new BigDecimal("-1")).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "balance", "Initial balance cannot be negative");
        }

        @Test
        @DisplayName("Should return 200 when balance is 0")
        void shouldReturn200_WhenBalanceIsZero() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount().withBalance(BigDecimal.ZERO).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 200);

            AccountResponse accountResponse = updateResponse.as(AccountResponse.class);
            AccountAssertions.assertUpdatedAccountResponse(accountResponse, updateAccountRequest, id);
        }

        @Test
        @DisplayName("Should return 200 when balance contains the maximum allowed number of integer and fraction digits")
        void shouldReturn200_WhenBalanceHasMaximumAllowedPrecision() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount()
                    .withBalance(new BigDecimal("12345678901234567.12")).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 200);

            AccountResponse accountResponse = updateResponse.as(AccountResponse.class);
            AccountAssertions.assertUpdatedAccountResponse(accountResponse, updateAccountRequest, id);
        }

        @Test
        @DisplayName("Should return 400 when balance contains more than 2 decimal places")
        void shouldReturn400_WhenBalanceContainsMoreThan2FractionDigits() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount()
                    .withBalance(new BigDecimal("100.123")).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "balance", "Initial balance must contain up to 17 integer digits and 2 decimal places");
        }

        @Test
        @DisplayName("Should return 400 when balance contains more than 17 integer digits")
        void shouldReturn400_WhenBalanceContainsMoreThan17IntegerDigits() {
            Long id = createAccountAndGetId();

            UpdateAccountRequest updateAccountRequest = AccountDataFactory.validUpdateAccount()
                    .withBalance(new BigDecimal("123456789012345678.12")).build();
            Response updateResponse = accountClient.updateAccount(updateAccountRequest, id);
            ResponseAssertions.assertStatus(updateResponse, 400);

            ErrorResponse errorResponse = updateResponse.as(ErrorResponse.class);
            ErrorAssertions.assertBadRequest(errorResponse);
            ValidationAssertions.assertFieldErrors(errorResponse, "balance", "Initial balance must contain up to 17 integer digits and 2 decimal places");
        }
    }
}
