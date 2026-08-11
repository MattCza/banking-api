package com.bank.api.assertions;

import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.response.AccountResponse;

import static org.assertj.core.api.Assertions.assertThat;

public final class AccountAssertions {

    private AccountAssertions() {
    }

    public static void assertCreatedAccountResponse(AccountResponse actual, CreateAccountRequest expected) {
        assertThat(actual.id())
                .as("created account id")
                .isNotNull();

        assertThat(actual.ownerName())
                .isEqualTo(expected.ownerName());

        assertThat(actual.email())
                .isEqualTo(expected.email().toLowerCase());

        assertThat(actual.balance())
                .isEqualByComparingTo(expected.initialBalance());
    }


}
