package com.bank.api.client;

import com.bank.api.config.RestAssuredConfig;
import com.bank.api.auth.TokenProvider;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    protected RequestSpecification getRequestSpecification() {
        return RestAssuredConfig.requestSpecification();
    }

    protected RequestSpecification getJsonRequestSpecification() {
        return RestAssuredConfig.requestSpecification()
                .contentType(ContentType.JSON);
    }

    protected RequestSpecification getAuthenticatedRequestSpecification() {
        return getJsonRequestSpecification()
                .header("Authorization", "Bearer " + TokenProvider.getToken());
    }
}
