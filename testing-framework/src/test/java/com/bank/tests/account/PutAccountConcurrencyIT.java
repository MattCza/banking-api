package com.bank.tests.account;

import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.request.UpdateAccountRequest;
import com.bank.api.dto.response.AccountResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("concurrency")
public class PutAccountConcurrencyIT {

    private final AccountClient accountClient = new AccountClient();
    private final AuthClient authClient = new AuthClient();


    private Long createAccountAndGetId(String token) {
        Response response = accountClient.createAccount(AccountDataFactory.validCreateAccount().build(), token);
        return response.as(AccountResponse.class).id();
    }


    @Test
    @DisplayName("Should allow only one successful update when duplicate email requests run in parallel")
    void shouldReturn409Conflict_WhenParallelUpdatesTargetSameEmail() throws Exception {
        int numberOfRequests = 5;
        String token = authClient.loginAndGetToken(LoginDataFactory.validAdmin());

        List<Long> accountIds = List.of(
                createAccountAndGetId(token),
                createAccountAndGetId(token),
                createAccountAndGetId(token),
                createAccountAndGetId(token),
                createAccountAndGetId(token)
        );

        String contestedEmail = AccountDataFactory.validCreateAccount().build().email();
        UpdateAccountRequest updatePayload = AccountDataFactory.validUpdateAccount().withEmail(contestedEmail).build();

        CountDownLatch ready = new CountDownLatch(numberOfRequests);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);

        try {
            List<Future<Response>> futures = accountIds.stream()
                    .map(id -> executor.submit(() -> {
                        ready.countDown();
                        start.await();
                        return accountClient.updateAccount(updatePayload, id, token);
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
                    .sorted()
                    .toList();

            assertThat(statuses).hasSize(numberOfRequests)
                    .startsWith(200)
                    .containsOnly(200, 409)
                    .filteredOn(status -> status == 200)
                    .hasSize(1);
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }


}