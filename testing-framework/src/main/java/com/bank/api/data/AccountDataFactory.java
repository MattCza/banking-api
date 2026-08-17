package com.bank.api.data;

import java.util.ArrayList;
import java.util.List;

public final class AccountDataFactory {
    private AccountDataFactory() {
    }

    public static CreateAccountRequestBuilder validCreateAccount() {
        return new CreateAccountRequestBuilder();
    }

    public static UpdateAccountRequestBuilder validUpdateAccount() {
        return new UpdateAccountRequestBuilder();
    }

    public static String emailWithLength(int length) {
        if (length < 6) {
            throw new IllegalArgumentException("Email length must be at least 6 characters.");
        }

        int localPartLength = Math.min(64, length - 5);
        int domainLength = length - localPartLength - 1;
        int headLength = domainLength - 3;

        return "b".repeat(localPartLength) + "@" + buildDomainHead(headLength) + ".pl";
    }

    private static String buildDomainHead(int targetLength) {
        List<String> labels = new ArrayList<>();
        int remaining = targetLength;

        while (remaining > 63) {
            int nextLabelLength = Math.min(63, remaining - 2);
            labels.add("a".repeat(nextLabelLength));
            remaining -= nextLabelLength + 1;
        }

        labels.add("a".repeat(remaining));
        return String.join(".", labels);
    }
}
