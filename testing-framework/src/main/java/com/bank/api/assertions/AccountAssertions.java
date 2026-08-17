package com.bank.api.assertions;

import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.request.UpdateAccountRequest;
import com.bank.api.dto.response.AccountResponse;

import java.util.Locale;

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
                .isEqualTo(expected.email().trim().toLowerCase(Locale.ROOT));

        assertThat(actual.balance())
                .isEqualByComparingTo(expected.initialBalance());
    }

    public static void assertUpdatedAccountResponse(AccountResponse actual, UpdateAccountRequest expected, Long expectedId) {
        assertThat(actual.id())
                .as("updated account id")
                .isEqualTo(expectedId);

        assertThat(actual.ownerName())
                .isEqualTo(expected.ownerName());

        assertThat(actual.email())
                .isEqualTo(expected.email().trim().toLowerCase(Locale.ROOT));

        assertThat(actual.balance())
                .isEqualByComparingTo(expected.balance());
    }


}
