package com.bank.api.client;

import com.bank.api.config.RestAssuredConfig;
import com.bank.api.auth.TokenProvider;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseClient {


    protected RequestSpecification rawRequest() {
        return RestAssuredConfig.requestSpecification();
    }

    protected RequestSpecification request() {
        return rawRequest()
                .contentType(ContentType.JSON);
    }

    protected RequestSpecification authenticatedRequest() {
        return authenticatedRequest(TokenProvider.getToken());
    }

    protected RequestSpecification authenticatedRequest(String token) {
        return request()
                .header("Authorization", "Bearer " + token);
    }
}
