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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("functional")
public class GetAccountIT {

    private final AccountClient accountClient = new AccountClient();


    @Test
    @Tag("smoke")
    @DisplayName("Should return account details when account exists")
    void shouldReturnAccount_WhenAccountExists() {
        CreateAccountRequest request = AccountDataFactory.validCreateAccount().build();

        Response response = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(response, 201);
        AccountResponse createdAccount = response.as(AccountResponse.class);

        Response getResponse = accountClient.getAccountById(createdAccount.id());
        ResponseAssertions.assertStatus(getResponse, 200);
        ResponseAssertions.assertMatchesSchema(getResponse, "schemas/account-response-schema.json");
        AccountResponse fetchedAccount = getResponse.as(AccountResponse.class);

        assertThat(fetchedAccount.id()).isEqualTo(createdAccount.id());
        AccountAssertions.assertCreatedAccountResponse(fetchedAccount, request);
    }

    @ParameterizedTest
    @ValueSource(longs = {99999L, 0L, -1L})
    @DisplayName("Should return 404 error response when account does not exist")
    void shouldReturn404_WhenAccountDoesNotExist(long accountId) {
        Response response = accountClient.getAccountById(accountId);
        ResponseAssertions.assertStatus(response, 404);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }

    // Error handling in BE -> DONE
    @Test
    @DisplayName("Should return 400 when account ID is not a number")
    void shouldReturn400_WhenAccountIdIsNonNumeric() {
        Response response = accountClient.getAccountById("asd");
        ResponseAssertions.assertStatus(response, 400);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertInvalidPathParameter(errorResponse);
    }

}