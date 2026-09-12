package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("functional")
public class PostAccountIT {

    AccountClient accountClient = new AccountClient();

    @Test
    @Tag("smoke")
    @DisplayName("Should successfully open a new bank account when provided a valid payload")
    public void shouldCreateAccountSuccessfully_WhenPayloadIsValid() {
        // Arrange
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();

        // Act
        Response response = accountClient.createAccount(request);

        // Assert
        ResponseAssertions.assertStatus(response, 201);
        ResponseAssertions.assertMatchesSchema(response, "schemas/account-response-schema.json");
        AccountResponse createdAccount = response.as(AccountResponse.class);

        AccountAssertions.assertCreatedAccountResponse(createdAccount, request);
    }

    @Test
    @DisplayName("Should fail to create an account when the email address already exists")
    public void shouldReturn409Conflict_WhenEmailIsDuplicate() {
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();

        Response response = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(response, 201);

        Response errorResponse = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(errorResponse, 409);
        ResponseAssertions.assertMatchesSchema(errorResponse, "schemas/error-response-schema.json");

        ErrorResponse error = errorResponse.as(ErrorResponse.class);
        ErrorAssertions.assertConflict(error);
    }


}
