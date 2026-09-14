package com.bank.api.client;

import com.bank.api.dto.request.TransactionRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class TransactionClient extends BaseClient {
    private static final String ACCOUNTS_ENDPOINT = "/accounts";

    private Response executeDeposit(TransactionRequest request, Long accountId, RequestSpecification specification) {
        return given()
                .spec(specification)
                .pathParam("id", accountId)
                .body(request)
                .post(ACCOUNTS_ENDPOINT + "/{id}/deposit");
    }

    public Response deposit(TransactionRequest request, Long accountId) {
        return executeDeposit(request, accountId, authenticatedRequest());
    }

    public Response deposit(TransactionRequest request, Long accountId, String token) {
        return executeDeposit(request, accountId, authenticatedRequest(token));
    }

    private Response executeWithdraw(TransactionRequest request, Long accountId, RequestSpecification specification) {
        return given()
                .spec(specification)
                .pathParam("id", accountId)
                .body(request)
                .post(ACCOUNTS_ENDPOINT + "/{id}/withdraw");
    }

    public Response withdraw(TransactionRequest request, Long accountId) {
        return executeWithdraw(request, accountId, authenticatedRequest());
    }

    public Response withdraw(TransactionRequest request, Long accountId, String token) {
        return executeWithdraw(request, accountId, authenticatedRequest(token));
    }
}
