package com.bank.tests.transaction;

import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.TransactionAssertions;
import com.bank.api.client.TransactionClient;
import com.bank.api.data.TransactionDataFactory;
import com.bank.api.dto.TransactionType;
import com.bank.api.dto.request.TransactionRequest;
import com.bank.api.dto.response.ErrorResponse;
import com.bank.api.dto.response.TransactionResponse;
import com.bank.tests.account.BaseAccountIT;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("functional")
public class PostDepositIT extends BaseAccountIT {

    private final TransactionClient transactionClient = new TransactionClient();

    @Test
    @Tag("smoke")
    @DisplayName("Should successfully deposit money into an existing account")
    void shouldDeposit_WhenAccountExists() {
        Long accountId = createAccountAndGetId();
        TransactionRequest request = TransactionDataFactory.validDeposit().build();

        Response response = transactionClient.deposit(request, accountId);
        ResponseAssertions.assertStatus(response, 201);
        ResponseAssertions.assertMatchesSchema(response, "schemas/transaction-response-schema.json");

        TransactionResponse transaction = response.as(TransactionResponse.class);
        TransactionAssertions.assertTransactionResponse(transaction, request, accountId, TransactionType.DEPOSIT);

        Response getResponse = accountClient.getAccountById(accountId);
        ResponseAssertions.assertStatus(getResponse, 200);
    }

    @Test
    @DisplayName("Should return 404 when depositing into a non-existing account")
    void shouldReturn404_WhenAccountDoesNotExist() {
        TransactionRequest request = TransactionDataFactory.validDeposit().build();

        Response response = transactionClient.deposit(request, 999999L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }
}
