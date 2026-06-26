package com.bank.api.client;

import com.bank.api.dto.request.CreateAccountRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AccountClient extends BaseClient {

    private static final String ACCOUNTS_ENDPOINT = "/accounts";

    public Response createAccount(CreateAccountRequest payload) {
        return given()
                .spec(getRequestSpecification())
                .body(payload)
                .when()
                .post(ACCOUNTS_ENDPOINT);
    }

    public Response getAccountById(Long accountId) {
        return given()
                .spec(getRequestSpecification())
                .pathParam("id", accountId)
                .when()
                .get(ACCOUNTS_ENDPOINT + "/{id}");
    }

    public Response deleteAccountById(Long accountId) {
        return given()
                .spec(getRequestSpecification())
                .pathParam("id", accountId)
                .when()
                .delete(ACCOUNTS_ENDPOINT + "/{id}");
    }
}
