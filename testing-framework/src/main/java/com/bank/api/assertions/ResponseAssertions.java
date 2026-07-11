package com.bank.api.assertions;

import io.restassured.response.Response;
import org.apache.http.entity.ContentType;

import static org.assertj.core.api.Assertions.assertThat;

public final class ResponseAssertions {

    private ResponseAssertions() {

    }

    public static void assertStatus(Response response, int expectedStatus) {
        if (response.statusCode() != expectedStatus) {
            System.out.println("Expected status: " + expectedStatus);
            System.out.println("Actual status: " + response.statusCode());

            System.out.println("Response:");
            response.prettyPrint();
        }

        assertThat(response.statusCode())
                .as("HTTP status")
                .isEqualTo(expectedStatus);
    }

    public static void assertContentTypeJson(Response response) {
        assertThat(response.contentType())
                .as("Content-type")
                .startsWith("application/json");
    }

    public static void assertJsonResponse(Response response, int expectedStatus) {
        assertStatus(response, expectedStatus);
        assertContentTypeJson(response);
    }
}
