package com.bank.api.client;

import com.bank.api.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    protected RequestSpecification getRequestSpecification() {
        return RestAssuredConfig.requestSpecification();
    }
}
