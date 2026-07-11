package com.bank.api.auth;

import com.bank.api.client.AuthClient;
import com.bank.api.dto.request.LoginRequest;

public final class TokenProvider {
    private static String token;

    private TokenProvider() {
    }

    public static String getToken() {
        if (token == null) {
            LoginRequest request = new LoginRequest("admin", "admin123");
        }
        return token;
    }
}
