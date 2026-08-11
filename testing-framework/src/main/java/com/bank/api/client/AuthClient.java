package com.bank.api.client;

import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.dto.request.LoginRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class AuthClient extends BaseClient {
    private static final String LOGIN_ENDPOINT = "/auth/login";



    private Response executeLogin(Object body, RequestSpecification specification) {
        return given()
                .spec(specification)
                .body(body)
                .post(LOGIN_ENDPOINT);
    }

    public Response login(LoginRequest request) {
        return executeLogin(request, request());
    }

    public Response login(String request) {
        return executeLogin(request, request());
    }

    public String loginAndGetToken(LoginRequest request) {
        Response response = login(request);
        ResponseAssertions.assertStatus(response, 200);

        return response.jsonPath().getString("token");
    }

    public Response loginWithContentType(String request, String contentType) {
        return executeLogin(request, rawRequest().contentType(contentType));
    }

    public Response loginWithoutContentType(String request) {
        return executeLogin(request, rawRequest());
    }
}
