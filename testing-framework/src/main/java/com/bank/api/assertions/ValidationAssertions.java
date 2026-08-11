package com.bank.api.assertions;

import com.bank.api.dto.response.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationAssertions {

    public static void assertFieldError(ErrorResponse error, String field, String expectedMessage) {
        assertThat(error.validationErrors()).containsKey(field);
        assertThat(error.validationErrors().get(field)).contains(expectedMessage);
    }

    public static void assertFieldErrors(ErrorResponse error, String field, String... expectedMessages) {
        assertThat(error.validationErrors()).containsKey(field);
        assertThat(error.validationErrors().get(field)).containsExactlyInAnyOrder(expectedMessages);
    }

    public static void assertFieldExists(ErrorResponse error, String... fields) {
        assertThat(error.validationErrors()).containsOnlyKeys(fields);
    }
}
