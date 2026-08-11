package com.bank.api.client;

import com.bank.api.dto.request.CreateAccountRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class AccountClient extends BaseClient {
    private static final String ACCOUNTS_ENDPOINT = "/accounts";



    private Response executeCreateAccount(CreateAccountRequest request, RequestSpecification specification) {
        return given()
                .spec(specification)
                .body(request)
                .post(ACCOUNTS_ENDPOINT);
    }

    public Response createAccount(CreateAccountRequest request) {
        return executeCreateAccount(request, authenticatedRequest());
    }

    public Response createAccount(CreateAccountRequest request, String token) {
        return executeCreateAccount(request, authenticatedRequest(token));
    }

    public Response createAccountWithoutToken(CreateAccountRequest request) {
        return executeCreateAccount(request, request());
    }



    private Response executeGetAccountById(Long accountId, RequestSpecification specification) {
        return given()
                .spec(specification)
                .pathParam("id", accountId)
                .get(ACCOUNTS_ENDPOINT + "/{id}");
    }

    private Response executeGetAccountById(String accountId, RequestSpecification specification) {
        return given()
                .spec(specification)
                .pathParam("id", accountId)
                .get(ACCOUNTS_ENDPOINT + "/{id}");
    }

    public Response getAccountById(Long accountId) {
        return executeGetAccountById(accountId, authenticatedRequest());
    }

    public Response getAccountById(String accountId) {
        return executeGetAccountById(accountId, authenticatedRequest());
    }

    public Response getAccountById(Long accountId, String token) {
        return executeGetAccountById(accountId, authenticatedRequest(token));
    }

    public Response getAccountByIdWithoutToken(Long accountId) {
        return executeGetAccountById(accountId, request());
    }



    private Response executeDeleteAccountById(Long accountId, RequestSpecification specification) {
        return given()
                .spec(specification)
                .pathParam("id", accountId)
                .delete(ACCOUNTS_ENDPOINT + "/{id}");
    }

    public Response deleteAccountById(Long accountId) {
        return executeDeleteAccountById(accountId, authenticatedRequest());
    }

    public Response deleteAccountById(Long accountId, String token) {
        return executeDeleteAccountById(accountId, authenticatedRequest(token));

    }

    public Response deleteAccountByIdWithoutToken(Long accountId) {
        return executeDeleteAccountById(accountId, request());
    }
}
