package com.bank.api.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RestAssuredConfig {

    private static final Config CONFIG = Config.load();

    private RestAssuredConfig() {
    }

    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(CONFIG.getBaseUrl())
                .setPort(CONFIG.getPort())
                .setBasePath(CONFIG.getBasePath())
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();
    }




}
