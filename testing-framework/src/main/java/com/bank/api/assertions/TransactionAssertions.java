package com.bank.api.assertions;

import com.bank.api.dto.TransactionType;
import com.bank.api.dto.request.TransactionRequest;
import com.bank.api.dto.response.TransactionResponse;

import static org.assertj.core.api.Assertions.assertThat;

public final class TransactionAssertions {

    private TransactionAssertions() {
    }

    public static void assertTransactionResponse(TransactionResponse actual, TransactionRequest expected,
                                                   Long expectedAccountId, TransactionType expectedType) {
        assertThat(actual.id())
                .as("transaction id")
                .isNotNull();

        assertThat(actual.accountId())
                .isEqualTo(expectedAccountId);

        assertThat(actual.money().amount())
                .isEqualByComparingTo(expected.amount());

        assertThat(actual.money().currency())
                .isEqualTo(expected.currency());

        assertThat(actual.type())
                .isEqualTo(expectedType);

        assertThat(actual.createdAt())
                .as("transaction timestamp")
                .isNotNull();
    }
}
