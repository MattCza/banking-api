package com.bank.api.auth;

import com.bank.api.client.AuthClient;
import com.bank.api.dto.request.LoginRequest;
import io.restassured.response.Response;

public final class TokenProvider {
    private static String token;

    private TokenProvider() {
    }

    public static String getToken() {
        if (token == null) {
            LoginRequest request = new LoginRequest("admin", "admin123");
            Response response = new AuthClient().login(request);

            token = response.jsonPath().getString("token");
        }
        return token;
    }
}
