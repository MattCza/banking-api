package com.bank.tests.transaction;

import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.client.TransactionClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.data.TransactionDataFactory;
import com.bank.api.dto.request.TransactionRequest;
import com.bank.api.dto.response.AccountResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("concurrency")
public class PostWithdrawalConcurrencyIT {

    private final AccountClient accountClient = new AccountClient();
    private final TransactionClient transactionClient = new TransactionClient();
    private final AuthClient authClient = new AuthClient();

    @Test
    @DisplayName("Should never let parallel withdrawals push the balance negative")
    void shouldNotOverdraw_WhenWithdrawalsRunInParallel() throws Exception {
        int numberOfRequests = 5;
        BigDecimal startingBalance = new BigDecimal("100.00");
        BigDecimal withdrawalAmount = new BigDecimal("30.00");

        String token = authClient.loginAndGetToken(LoginDataFactory.validAdmin());

        Response createResponse = accountClient.createAccount(
                AccountDataFactory.validCreateAccount().withBalance(startingBalance).build(), token);
        ResponseAssertions.assertStatus(createResponse, 201);
        Long accountId = createResponse.as(AccountResponse.class).id();

        TransactionRequest withdrawalRequest = TransactionDataFactory.validWithdrawal()
                .withAmount(withdrawalAmount)
                .build();

        CountDownLatch ready = new CountDownLatch(numberOfRequests);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);

        try {
            List<Future<Response>> futures = IntStream.range(0, numberOfRequests)
                    .mapToObj(i -> executor.submit(() -> {
                        ready.countDown();
                        start.await();
                        return transactionClient.withdraw(withdrawalRequest, accountId, token);
                    }))
                    .toList();

            ready.await();
            start.countDown();

            List<Integer> statuses = futures.stream()
                    .map(future -> {
                        try {
                            return future.get().statusCode();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .toList();

            assertThat(statuses).hasSize(numberOfRequests).containsOnly(201, 409);

            long successfulWithdrawals = statuses.stream().filter(status -> status == 201).count();
            BigDecimal expectedBalance = startingBalance.subtract(withdrawalAmount.multiply(BigDecimal.valueOf(successfulWithdrawals)));

            Response getResponse = accountClient.getAccountById(accountId, token);
            ResponseAssertions.assertStatus(getResponse, 200);
            BigDecimal actualBalance = getResponse.as(AccountResponse.class).money().amount();

            assertThat(actualBalance)
                    .as("balance after %d successful withdrawal(s) of %s", successfulWithdrawals, withdrawalAmount)
                    .isEqualByComparingTo(expectedBalance);

            assertThat(actualBalance)
                    .as("account must never go negative")
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
