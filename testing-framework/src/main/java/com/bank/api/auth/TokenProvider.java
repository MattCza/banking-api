package com.bank.api.auth;

import com.bank.api.client.AuthClient;
import com.bank.api.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class TokenProvider {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final long REFRESH_BUFFER_MILLIS = 1000L;

    private static String token;
    private static long tokenExpiresAtMillis;

    private TokenProvider() {
    }

    public static synchronized String getToken() {
        if (token == null || isTokenExpired()) {
            refreshToken();
        }

        return token;
    }

    public static synchronized void clear() {
        token = null;
        tokenExpiresAtMillis = 0L;
    }

    private static void refreshToken() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        token = new AuthClient().loginAndGetToken(request);
        tokenExpiresAtMillis = extractExpiration(token);
    }

    private static boolean isTokenExpired() {
        return System.currentTimeMillis() + REFRESH_BUFFER_MILLIS >= tokenExpiresAtMillis;
    }

    private static long extractExpiration(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode payloadNode = OBJECT_MAPPER.readTree(payload);

            return payloadNode.get("exp").asLong() * 1000L;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to extract expiration from JWT.", ex);
        }
    }
}
