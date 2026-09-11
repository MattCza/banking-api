package com.bank.service;

import com.bank.dto.auth.LoginRequest;
import com.bank.dto.auth.LoginResponse;
import com.bank.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserDetails userDetails(String username) {
        return User.withUsername(username)
                .password("encoded-password")
                .roles("ADMIN")
                .build();
    }

    @Test
    void shouldReturnToken_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        UserDetails principal = userDetails("admin");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(principal)).thenReturn("mocked-jwt-token");

        LoginResponse response = authenticationService.login(request);

        assertThat(response.token()).isEqualTo("mocked-jwt-token");
    }

    @Test
    void shouldAuthenticateWithExactUsernameAndPassword_FromRequest() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        UserDetails principal = userDetails("admin");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        when(authenticationManager.authenticate(captor.capture())).thenReturn(authentication);
        when(jwtService.generateToken(principal)).thenReturn("mocked-jwt-token");

        authenticationService.login(request);

        assertThat(captor.getValue().getPrincipal()).isEqualTo("admin");
        assertThat(captor.getValue().getCredentials()).isEqualTo("admin123");
    }

    @Test
    void shouldPropagateException_WhenAuthenticationFails() {
        LoginRequest request = new LoginRequest("admin", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authenticationService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtService);
    }
}
