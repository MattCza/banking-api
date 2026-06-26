package com.bank.api.assertions;

import com.bank.api.dto.response.ErrorResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public final class ErrorAssertions {

    private ErrorAssertions() {
    }

    public static void assertDuplicateEmailConflict(ErrorResponse actual) {
        assertThat(actual.status())
                .isEqualTo(409);

        assertThat(actual.error())
                .isEqualTo("CONFLICT");

        assertThat(actual.message())
                .contains("already exists.");

        assertThat(actual.validationErrors())
                .isEmpty();
    }

    public static void assertInvalidRequestFields(ErrorResponse actual, String... expectedFields) {
        assertThat(actual.validationErrors())
                .containsOnlyKeys(expectedFields);
    }

    public static void assertNotFoundIdRequest(ErrorResponse actual) {
        assertThat(actual.status())
                .isEqualTo(404);

        assertThat(actual.error())
                .isEqualTo("NOT_FOUND");

        assertThat(actual.message())
                .contains("not found");

        assertThat(actual.validationErrors())
                .isEmpty();
    }


}