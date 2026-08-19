package com.bank.tests.account;

import com.bank.api.client.AccountClient;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountConcurrencyIT {

    private final AccountClient accountClient = new AccountClient();
    private final AuthClient authClient = new AuthClient();

    @Test
    @DisplayName("Should allow only one successful create when duplicate email requests run in parallel")
    void shouldReturn409Conflict_WhenParallelEmailIsDuplicate() throws Exception {
        int numberOfRequests = 10;
        CreateAccountRequest payload = AccountDataFactory.validCreateAccount().build();

        String token = authClient.loginAndGetToken(LoginDataFactory.validAdmin());

        CountDownLatch ready = new CountDownLatch(numberOfRequests);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);

        try {
            List<Future<Response>> futures = IntStream.range(0, numberOfRequests)
                    .mapToObj(i -> executor.submit(() -> {
                        ready.countDown();
                        start.await();
                        return accountClient.createAccount(payload, token);
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
                    .startsWith(201)
                    .containsOnly(201, 409)
                    .filteredOn(status -> status == 201)
                    .hasSize(1);
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
