package com.bank.tests.account;

import com.bank.api.assertions.AccountAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.request.UpdateAccountRequest;
import com.bank.api.dto.response.AccountResponse;
import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PutAccountIT {

    private final AccountClient accountClient = new AccountClient();

    @Test
    @DisplayName("Should update account details when account exists")
    void shouldUpdateAccount_WhenAccountExists() {
        Response createAccountResponse = accountClient.createAccount(
                AccountDataFactory.validCreateAccount()
                        .withOwnerName("John Doe")
                        .withEmail("John@Doe.xyz")
                        .withBalance(new BigDecimal("1234.00"))
                        .build());

        ResponseAssertions.assertStatus(createAccountResponse, 201);
        Long createAccountId = createAccountResponse.as(AccountResponse.class).id();

        UpdateAccountRequest request = AccountDataFactory.validUpdateAccount()
                .withOwnerName("Jane Doe")
                .withEmail("Jane@Doe.xyz")
                .withBalance(new BigDecimal("1337.00"))
                .build();

        Response updatedAccountResponse = accountClient.updateAccount(request, createAccountId);
        ResponseAssertions.assertStatus(updatedAccountResponse, 200);

        Response getResponse = accountClient.getAccountById(createAccountId);
        ResponseAssertions.assertStatus(getResponse, 200);
        AccountResponse fetchedAccount = getResponse.as(AccountResponse.class);

        AccountAssertions.assertUpdatedAccountResponse(fetchedAccount, request, createAccountId);
    }

    @Test
    @DisplayName("Should return 409 when updating account with email belonging to another account")
    void shouldReturn409_WhenEmailBelongsToAnotherAccount() {
        CreateAccountRequest firstAccountRequest = AccountDataFactory.validCreateAccount()
                .withOwnerName("John Doe")
                .withEmail("John@Doe.xyz")
                .withBalance(new BigDecimal("1234.00"))
                .build();

        Response firstCreateAccountResponse = accountClient.createAccount(firstAccountRequest);
        ResponseAssertions.assertStatus(firstCreateAccountResponse, 201);
        Long firstAccountId = firstCreateAccountResponse.as(AccountResponse.class).id();

        CreateAccountRequest secondAccountRequest = AccountDataFactory.validCreateAccount()
                .withOwnerName("Jane Doe")
                .withEmail("Jane@Doe.xyz")
                .withBalance(new BigDecimal("2000.00"))
                .build();

        Response secondCreateAccountResponse = accountClient.createAccount(secondAccountRequest);
        ResponseAssertions.assertStatus(secondCreateAccountResponse, 201);

        UpdateAccountRequest request = AccountDataFactory.validUpdateAccount()
                .withOwnerName("John Updated")
                .withEmail(secondAccountRequest.email())
                .withBalance(new BigDecimal("1337.00"))
                .build();

        Response updatedAccountResponse = accountClient.updateAccount(request, firstAccountId);
        ResponseAssertions.assertStatus(updatedAccountResponse, 409);

        ErrorAssertions.assertConflict(updatedAccountResponse.as(ErrorResponse.class));

        Response getResponse = accountClient.getAccountById(firstAccountId);
        ResponseAssertions.assertStatus(getResponse, 200);
        AccountResponse fetchedAccount = getResponse.as(AccountResponse.class);

        AccountAssertions.assertCreatedAccountResponse(fetchedAccount, firstAccountRequest);
    }

    @Test
    @DisplayName("Should update account successfully when email remains unchanged")
    void shouldUpdateAccount_WhenEmailRemainsUnchanged() {
        CreateAccountRequest createAccountRequest = AccountDataFactory.validCreateAccount()
                .withOwnerName("Joe Doe")
                .withEmail("Joe@Doe.xyz")
                .withBalance(new BigDecimal("1111.00"))
                .build();

        Response createAccountResponse = accountClient.createAccount(createAccountRequest);
        ResponseAssertions.assertStatus(createAccountResponse, 201);
        Long createAccountId = createAccountResponse.as(AccountResponse.class).id();

        UpdateAccountRequest request = AccountDataFactory.validUpdateAccount()
                .withOwnerName("John Doe")
                .withEmail("Joe@Doe.xyz")
                .withBalance(new BigDecimal("1337.00"))
                .build();

        Response updatedAccountResponse = accountClient.updateAccount(request, createAccountId);
        ResponseAssertions.assertStatus(updatedAccountResponse, 200);

        Response getResponse = accountClient.getAccountById(createAccountId);
        ResponseAssertions.assertStatus(getResponse, 200);
        AccountResponse fetchedAccount = getResponse.as(AccountResponse.class);

        AccountAssertions.assertUpdatedAccountResponse(fetchedAccount, request, createAccountId);
    }
}
