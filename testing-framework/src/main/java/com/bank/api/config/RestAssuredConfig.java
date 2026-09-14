package com.bank.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
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
                .setConfig(io.restassured.config.RestAssuredConfig.config()
                        .objectMapperConfig(objectMapperConfig()))
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();
    }

    private static ObjectMapperConfig objectMapperConfig() {
        return new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        });
    }
}
