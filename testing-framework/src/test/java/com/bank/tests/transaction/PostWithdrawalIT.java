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

import java.math.BigDecimal;

@Tag("functional")
public class PostWithdrawalIT extends BaseAccountIT {

    private final TransactionClient transactionClient = new TransactionClient();

    @Test
    @Tag("smoke")
    @DisplayName("Should successfully withdraw money from an existing account")
    void shouldWithdraw_WhenBalanceIsSufficient() {
        Long accountId = createAccountAndGetId();
        TransactionRequest request = TransactionDataFactory.validWithdrawal()
                .withAmount(new BigDecimal("10.00"))
                .build();

        Response response = transactionClient.withdraw(request, accountId);
        ResponseAssertions.assertStatus(response, 201);
        ResponseAssertions.assertMatchesSchema(response, "schemas/transaction-response-schema.json");

        TransactionResponse transaction = response.as(TransactionResponse.class);
        TransactionAssertions.assertTransactionResponse(transaction, request, accountId, TransactionType.WITHDRAWAL);

        Response getResponse = accountClient.getAccountById(accountId);
        ResponseAssertions.assertStatus(getResponse, 200);
    }

    @Test
    @DisplayName("Should return 404 when withdrawing from a non-existing account")
    void shouldReturn404_WhenAccountDoesNotExist() {
        TransactionRequest request = TransactionDataFactory.validWithdrawal().build();

        Response response = transactionClient.withdraw(request, 999999L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }
}
