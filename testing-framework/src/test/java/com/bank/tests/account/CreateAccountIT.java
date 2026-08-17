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
import org.junit.jupiter.api.Test;

public class CreateAccountIT {

    AccountClient accountClient = new AccountClient();

    @Test
    @DisplayName("Should successfully open a new bank account when provided a valid payload")
    public void shouldCreateAccountSuccessfully_WhenPayloadIsValid() {
        // Arrange
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();

        // Act
        Response response = accountClient.createAccount(request);

        // Assert
        ResponseAssertions.assertStatus(response, 201);
        AccountResponse createdAccount = response.as(AccountResponse.class);

        // Assert id != null
        // createdAccount -> ownerName, email, balance == request ownerName, email, balance
        // (request data == response data)
        AccountAssertions.assertCreatedAccountResponse(createdAccount, request);
    }

    @Test
    @DisplayName("Should fail to create an account when the email address already exists")
    public void shouldReturn409Conflict_WhenEmailIsDuplicate() {
        // Arrange
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();

        // Act
        Response response = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(response, 201);

        // Act - attempt to use 2nd time the same e-mail
        Response errorResponse = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(errorResponse, 409);

        // Assert - ErrorResponse from duplicateResponse
        ErrorResponse error = errorResponse.as(ErrorResponse.class);
        ErrorAssertions.assertConflict(error);
    }

    
}
