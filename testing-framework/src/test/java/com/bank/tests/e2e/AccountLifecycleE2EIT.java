package com.bank.tests.e2e;

import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.client.TransactionClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.data.TransactionDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.request.TransactionRequest;
import com.bank.api.dto.response.AccountResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("functional")
public class AccountLifecycleE2EIT {

    private final AuthClient authClient = new AuthClient();
    private final AccountClient accountClient = new AccountClient();
    private final TransactionClient transactionClient = new TransactionClient();

    @Test
    @Tag("smoke")
    @DisplayName("Should complete the full account lifecycle: login, create, deposit, withdraw, verify final balance")
    void shouldCompleteFullAccountLifecycle() {
        BigDecimal startingBalance = new BigDecimal("100.00");
        BigDecimal depositAmount = new BigDecimal("50.00");
        BigDecimal withdrawalAmount = new BigDecimal("20.00");
        BigDecimal expectedFinalBalance = startingBalance.add(depositAmount).subtract(withdrawalAmount);

        // login
        String token = authClient.loginAndGetToken(LoginDataFactory.validAdmin());

        // create account
        CreateAccountRequest createRequest = AccountDataFactory.validCreateAccount()
                .withBalance(startingBalance)
                .build();
        Response createResponse = accountClient.createAccount(createRequest, token);
        ResponseAssertions.assertStatus(createResponse, 201);
        Long accountId = createResponse.as(AccountResponse.class).id();

        // get account - confirm initial state
        Response getAfterCreate = accountClient.getAccountById(accountId, token);
        ResponseAssertions.assertStatus(getAfterCreate, 200);
        assertThat(getAfterCreate.as(AccountResponse.class).money().amount())
                .isEqualByComparingTo(startingBalance);

        // deposit
        TransactionRequest depositRequest = TransactionDataFactory.validDeposit()
                .withAmount(depositAmount)
                .build();
        Response depositResponse = transactionClient.deposit(depositRequest, accountId, token);
        ResponseAssertions.assertStatus(depositResponse, 201);

        // withdraw
        TransactionRequest withdrawalRequest = TransactionDataFactory.validWithdrawal()
                .withAmount(withdrawalAmount)
                .build();
        Response withdrawalResponse = transactionClient.withdraw(withdrawalRequest, accountId, token);
        ResponseAssertions.assertStatus(withdrawalResponse, 201);

        // get account - verify final state
        Response finalGetResponse = accountClient.getAccountById(accountId, token);
        ResponseAssertions.assertStatus(finalGetResponse, 200);

        assertThat(finalGetResponse.as(AccountResponse.class).money().amount())
                .as("balance after deposit of %s and withdrawal of %s from a starting balance of %s",
                        depositAmount, withdrawalAmount, startingBalance)
                .isEqualByComparingTo(expectedFinalBalance);
    }
}
