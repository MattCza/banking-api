package com.bank.api.data;

public final class TransactionDataFactory {
    private TransactionDataFactory() {
    }

    public static TransactionRequestBuilder validDeposit() {
        return new TransactionRequestBuilder();
    }

    public static TransactionRequestBuilder validWithdrawal() {
        return new TransactionRequestBuilder();
    }
}
