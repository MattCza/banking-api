package com.bank.api.assertions;

import com.bank.api.dto.response.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationAssertions {

    public static void assertFieldError(ErrorResponse error, String field, String expectedMessage) {
        assertThat(error.validationErrors()).containsEntry(field, expectedMessage);
    }
}