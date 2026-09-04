package com.bank.tests.account;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("functional")
public class DeleteAccountIT {

    private final AccountClient accountClient = new AccountClient();


    @Test
    @Tag("smoke")
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccount_WhenAccountExists() {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build());
        ResponseAssertions.assertStatus(response, 201);
        Long accountId = response.as(AccountResponse.class).id();

        Response deleteResponse = accountClient.deleteAccountById(accountId);
        ResponseAssertions.assertStatus(deleteResponse, 204);

        Response getResponse = accountClient.getAccountById(accountId);
        ResponseAssertions.assertStatus(getResponse, 404);
    }

    @Test
    @DisplayName("Should return 404 when attempting to delete non-existing account")
    void shouldReturn404_WhenDeletingNonExistingAccount() {
        Response response = accountClient.deleteAccountById(999999L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }

    @Test
    @DisplayName("Should return 400 when attempting to delete account with non-numeric ID")
    void shouldReturn400_WhenDeletingNonNumericId() {
        Response response = accountClient.deleteAccountById("asd");
        ResponseAssertions.assertStatus(response, 400);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertInvalidPathParameter(errorResponse);
    }
}