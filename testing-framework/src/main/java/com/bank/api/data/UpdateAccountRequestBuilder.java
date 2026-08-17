package com.bank.api.data;

import com.bank.api.dto.request.UpdateAccountRequest;

public class UpdateAccountRequestBuilder extends BaseAccountRequestBuilder<UpdateAccountRequestBuilder> {
    public UpdateAccountRequest build() {
        return new UpdateAccountRequest(ownerName, email, balance);
    }
}
