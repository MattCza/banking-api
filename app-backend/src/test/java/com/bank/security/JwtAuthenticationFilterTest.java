package com.bank.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final FilterChain filterChain = mock(FilterChain.class);

    @AfterEach
    void clearSecurityContext() {
        // SecurityContextHolder is a static ThreadLocal - without this authentication set by one test would leak into the next one
        SecurityContextHolder.clearContext();
    }

    private UserDetails userDetails(String username) {
        return User.withUsername(username)
                .password("encoded-password")
                .roles("ADMIN")
                .build();
    }

    @Test
    void shouldContinueChainWithoutAuthenticating_WhenAuthorizationHeaderIsMissing() throws Exception {
        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueChainWithoutAuthenticating_WhenHeaderIsNotBearer() throws Exception {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSetAuthentication_WhenTokenIsValid() throws Exception {
        request.addHeader("Authorization", "Bearer valid-token");
        UserDetails principal = userDetails("admin");

        when(jwtService.extractUsername("valid-token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(principal);
        when(jwtService.isTokenValid("valid-token", principal)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(principal);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticate_WhenTokenExtractionFails() throws Exception {
        request.addHeader("Authorization", "Bearer garbage");

        when(jwtService.extractUsername("garbage")).thenThrow(new RuntimeException("malformed token"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticate_WhenUserNoLongerExists() throws Exception {
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.extractUsername("valid-token")).thenReturn("ghost");
        when(userDetailsService.loadUserByUsername("ghost")).thenThrow(new UsernameNotFoundException("User not found"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticate_WhenTokenIsNotValidForUser() throws Exception {
        request.addHeader("Authorization", "Bearer stale-token");
        UserDetails principal = userDetails("admin");

        when(jwtService.extractUsername("stale-token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(principal);
        when(jwtService.isTokenValid("stale-token", principal)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotOverwriteExistingAuthentication_WhenAlreadyAuthenticated() throws Exception {
        request.addHeader("Authorization", "Bearer valid-token");
        UsernamePasswordAuthenticationToken existingAuthentication =
                new UsernamePasswordAuthenticationToken("someone-else", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        when(jwtService.extractUsername("valid-token")).thenReturn("admin");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuthentication);
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }
}
