package com.bank.api.assertions;

import com.bank.api.dto.response.LoginResponse;

import static org.assertj.core.api.Assertions.assertThat;

public final class AuthAssertions {

    private AuthAssertions() {

    }

    public static void assertLoggedInAccount(LoginResponse response) {
        assertThat(response.token())
                .as("JWT token should not be null or blank")
                .isNotBlank();

        assertThat(response.token().split("\\."))
                .as("JWT should contain exactly three parts - header, payload and signature")
                .hasSize(3);
    }
}
