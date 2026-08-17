package com.bank.api.data;

import com.bank.api.dto.request.CreateAccountRequest;

public class CreateAccountRequestBuilder extends BaseAccountRequestBuilder<CreateAccountRequestBuilder> {
    public CreateAccountRequest build() {
        return new CreateAccountRequest(ownerName, email, balance);
    }
}
