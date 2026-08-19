package com.bank.api.data;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTestTokenFactory {
    private static final String SECRET = System.getenv().getOrDefault(
            "JWT_SECRET",
            "7a6e7d2f16fcb64b1cb0e5d0a4f31dbb2d80f8b4e0f2db68c94b9");

    private JwtTestTokenFactory() {
    }

    public static String expiredToken(String username) {

        Date now = new Date();

        Date issuedAt = new Date(now.getTime() - 120_000);
        Date expiration = new Date(now.getTime() - 60_000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public static String validToken(String username) {

        Date now = new Date();

        Date expiration = new Date(now.getTime() + 60_000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}