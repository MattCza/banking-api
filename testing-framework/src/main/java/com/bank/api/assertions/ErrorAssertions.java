package com.bank.api.assertions;

import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public final class ErrorAssertions {

    private ErrorAssertions() {
    }

    private static void assertError(ErrorResponse actual, int expectedStatus, String expectedError, String expectedMessage) {
        assertThat(actual.status())
                .as("Status code")
                .isEqualTo(expectedStatus);

        assertThat(actual.error())
                .as("Error type")
                .isEqualTo(expectedError);

        assertThat(actual.message())
                .as("Error message")
                .contains(expectedMessage);
    }

    public static void assertInvalidRequestFields(ErrorResponse actual, String... expectedFields) {
        assertThat(actual.validationErrors()).containsOnlyKeys(expectedFields);
    }

    public static void assertConflict(ErrorResponse actual) {
        assertError(actual, 409, "CONFLICT","already exists.");
        assertThat(actual.validationErrors()).isEmpty();
    }

    public static void assertNotFound(ErrorResponse actual) {
        assertError(actual, 404, "NOT_FOUND","not found");
        assertThat(actual.validationErrors()).isEmpty();
    }

    public static void assertUnauthorized(ErrorResponse actual) {
        assertError(actual, 401, "UNAUTHORIZED","Invalid credentials");
        assertThat(actual.validationErrors()).isEmpty();
    }

    public static void assertUnauthorized(Response response) {
        ResponseAssertions.assertJsonResponse(response, 401);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");
        assertUnauthorized(response.as(ErrorResponse.class));
    }

    public static void assertBadRequest(ErrorResponse actual) {
        assertError(actual, 400, "BAD_REQUEST","Validation failed");
    }

    public static void assertMalformedJson(ErrorResponse actual) {
        assertError(actual, 400, "BAD_REQUEST","Malformed JSON request");
    }

    public static void assertInvalidPathParameter(ErrorResponse actual) {
        assertError(actual, 400, "BAD_REQUEST","Invalid path parameter");
    }

    public static void assertUnsupportedMediaType(ErrorResponse actual) {
        assertError(actual, 415, "UNSUPPORTED_MEDIA_TYPE","Unsupported Content-Type");
    }

    public static void assertForbidden(ErrorResponse actual) {
        assertError(actual, 403, "FORBIDDEN","Access denied");
    }

    public static void assertForbidden(Response response) {
        ResponseAssertions.assertJsonResponse(response, 403);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");
        assertForbidden(response.as(ErrorResponse.class));
    }

}