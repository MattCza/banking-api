package com.bank.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-key-must-be-at-least-256-bits-long!";

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 60_000L);
    }

    private UserDetails userDetails(String username) {
        return User.withUsername(username)
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    void shouldExtractUsername_FromGeneratedToken() {
        String token = jwtService.generateToken(userDetails("john"));

        assertThat(jwtService.extractUsername(token)).isEqualTo("john");
    }

    @Test
    void shouldReturnTrue_WhenTokenMatchesUserAndIsNotExpired() {
        UserDetails user = userDetails("john");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void shouldReturnFalse_WhenTokenBelongsToDifferentUser() {
        String token = jwtService.generateToken(userDetails("john"));

        assertThat(jwtService.isTokenValid(token, userDetails("someone-else"))).isFalse();
    }

    @Test
    void shouldReturnFalse_WhenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "expiration", -60_000L);
        UserDetails user = userDetails("john");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isFalse();
    }
}
