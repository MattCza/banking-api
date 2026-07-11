package com.bank.api.client;

import com.bank.api.dto.request.LoginRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class AuthClient extends BaseClient {

    private static final String LOGIN_ENDPOINT = "/auth/login";

    private Response executeLogin(RequestSpecification spec, Object body) {
        return given()
                .spec(spec)
                .body(body)
                .post(LOGIN_ENDPOINT);
    }

    public Response login(LoginRequest requestBody) {
        return executeLogin(getJsonRequestSpecification(), requestBody);
    }

    public Response login(String requestBody) {
        return executeLogin(getJsonRequestSpecification(), requestBody);
    }

    public Response loginWithContentType(String requestBody, String contentType) {
        RequestSpecification specification = getRequestSpecification().contentType(contentType);

        return executeLogin(specification, requestBody);
    }

    public Response loginWithoutContentType(String requestBody) {
        return executeLogin(getRequestSpecification(), requestBody);
    }
}
