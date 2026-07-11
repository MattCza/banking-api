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

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountIT {

    private final AccountClient accountClient = new AccountClient();

    @Test
    @DisplayName("Should successfully open a new bank account when provided a valid payload")
    public void shouldCreateAccountSuccessfully_WhenPayloadIsValid() {
        CreateAccountRequest request = AccountDataFactory.validAccount().build();
        Response response = accountClient.createAccount(request);

        // Assert status code 201
        ResponseAssertions.assertStatus(response, 201);


        AccountResponse createdAccount = response.as(AccountResponse.class);

        // Assert id != null && createdAccount -> ownerName, email, balance == request ownerName, email, balance
        // (request data == response data)
        AccountAssertions.assertCreatedAccountResponse(createdAccount, request);
    }

    @Test
    @DisplayName("Should fail to create an account when the email address already exists")
    public void shouldReturn409Conflict_WhenEmailIsDuplicate() {
        CreateAccountRequest duplicatePayload = AccountDataFactory.validAccount().build();
        Response response = accountClient.createAccount(duplicatePayload);

        // unique e-mail
        ResponseAssertions.assertStatus(response, 201);

        // attempt to use 2nd the same e-mail
        Response errorResponse = accountClient.createAccount(duplicatePayload);
        ResponseAssertions.assertStatus(errorResponse, 409);


        // Get ErrorResponse from duplicateResponse
        ErrorResponse error = errorResponse.as(ErrorResponse.class);
        ErrorAssertions.assertConflict(error);
    }

    @Test
    @DisplayName("Parallel - Should fail to create an account when the email address already exists")
    void shouldReturn409Conflict_Parallel_WhenParallelEmailIsDuplicate() throws Exception {
        int numberOfRequests = 5;

        CreateAccountRequest payload = AccountDataFactory.validAccount().build();

        CountDownLatch ready = new CountDownLatch(numberOfRequests);
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);

        try {

            List<Future<Response>> futures = IntStream.range(0, numberOfRequests)
                    .mapToObj(i -> executor.submit(() -> {
                        ready.countDown();     // I'm ready
                        start.await();         // Wait for everyone
                        return accountClient.createAccount(payload);
                    })).toList();

            // Wait until every thread is waiting on the start latch
            ready.await();

            // Fire all requests simultaneously
            start.countDown();

            // Collect responses AFTER releasing the latch
            List<Integer> statuses = futures.stream()
                    .map(f -> {
                        try {
                            return f.get().statusCode();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .sorted()
                    .toList();

            assertThat(statuses).containsExactly(201, 409, 409, 409, 409);

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @Test
    @DisplayName("Should return 400 with validation details when owner Name is empty")
    public void shouldReturn400BadRequest_WhenOwnerNameIsEmpty() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withOwnerName("").build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "ownerName");
    }

    @Test
    @DisplayName("Should return 400 with validation details when owner Name is null")
    public void shouldReturn400BadRequest_WhenOwnerNameIsNull() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withOwnerName(null).build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "ownerName");
    }

    @Test
    @DisplayName("Should return 400 with validation details when owner Name is too short")
    public void shouldReturn400BadRequest_WhenOwnerNameIsTooShort() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withOwnerName("Xe").build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "ownerName");
    }

    @Test
    @DisplayName("Should return 400 with validation details when owner Name is too long")
    public void shouldReturn400BadRequest_WhenOwnerNameIsTooLong() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withOwnerName("Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch").build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "ownerName");
    }

    @Test
    @DisplayName("Should return 400 with validation details when email is empty")
    public void shouldReturn400BadRequest_WhenEmailIsEmpty() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withEmail("").build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "email");
    }

    @Test
    @DisplayName("Should return 400 with validation details when email is null")
    public void shouldReturn400BadRequest_WhenEmailIsNull() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withEmail(null).build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "email");
    }

    @Test
    @DisplayName("Should return 400 with validation details when email is too long")
    public void shouldReturn400BadRequest_WhenEmailIsTooLong() {
        String email = "x".repeat(243) + "@example.com"; // 255 chars
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withEmail(email).build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "email");
    }

    @Test
    @DisplayName("Should return 400 with validation details when balance is null")
    public void shouldReturn400BadRequest_WhenBalanceIsNull() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withBalance(null).build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "initialBalance");
    }

    @Test
    @DisplayName("Should return 400 with validation details when balance is negative Value")
    public void shouldReturn400BadRequest_WhenBalanceIsNegativeValue() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withBalance(new BigDecimal(-1)).build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "initialBalance");
    }

    @Test
    @DisplayName("Should return 400 with validation details when balance has too many integers")
    public void shouldReturn400BadRequest_WhenBalanceHasTooManyIntegers() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withBalance(new BigDecimal("123456789012345678.00")).build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "initialBalance");
    }

    @Test
    @DisplayName("Should return 400 with validation details when balance has too many fractions positions")
    public void shouldReturn400BadRequest_WhenBalanceHasTooManyFractionsPositions() {
        CreateAccountRequest invalidPayload = AccountDataFactory.validAccount().withBalance(new BigDecimal("123.123")).build();
        Response errorResponse = accountClient.createAccount(invalidPayload);
        ResponseAssertions.assertStatus(errorResponse, 400);

        ErrorResponse error = errorResponse.as(ErrorResponse.class);

        ErrorAssertions.assertInvalidRequestFields(error, "initialBalance");
    }


    @Test
    @DisplayName("Should return account details when account exists")
    public void shouldReturnAccount_WhenAccountExists() {
        CreateAccountRequest request = AccountDataFactory.validAccount().build();

        Response response = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(response, 201);
        AccountResponse createdAccount = response.as(AccountResponse.class);


        Response getResponse = accountClient.getAccountById(createdAccount.id());
        ResponseAssertions.assertStatus(getResponse, 200);
        AccountResponse fetchedAccount = getResponse.as(AccountResponse.class);


        assertThat(fetchedAccount.id()).isEqualTo(createdAccount.id());
        AccountAssertions.assertCreatedAccountResponse(fetchedAccount, request);
    }

    @Test
    @DisplayName("Should return 404 error response when account does not exist")
    public void shouldReturn404ErrorResponse_WhenAccountDoesNotExist() {
        Response response = accountClient.getAccountById(999999L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }

    @Test
    @DisplayName("Should delete account successfully")
    public void shouldReturn204Response_WhenAttemptingToDeleteAnAccount() {
        CreateAccountRequest request = AccountDataFactory.validAccount().build();

        Response response = accountClient.createAccount(request);
        ResponseAssertions.assertStatus(response, 201);

        AccountResponse createdAccount = response.as(AccountResponse.class);

        Response deleteResponse = accountClient.deleteAccountById(createdAccount.id());
        ResponseAssertions.assertStatus(deleteResponse, 204);

        Response getResponse = accountClient.getAccountById(createdAccount.id());
        ResponseAssertions.assertStatus(getResponse, 404);
    }

    @Test
    @DisplayName("Should return 404 error response when account does not exist")
    public void shouldReturn404ErrorResponse_WhenAttemptToDeleteNotFoundAccount() {
        Response response = accountClient.deleteAccountById(999999L);
        ResponseAssertions.assertStatus(response, 404);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        ErrorAssertions.assertNotFound(errorResponse);
    }
}



