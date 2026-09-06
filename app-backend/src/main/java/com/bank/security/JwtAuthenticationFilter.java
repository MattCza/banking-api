package com.bank.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        String username;

        try {
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            filterChain.doFilter(request, response);
            return;
        }


        // DIAGNOSE - JWT username; UserDetails username; Token valid;
//        try {
//            username = jwtService.extractUsername(jwt);
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                System.out.println("JWT username = " + username);
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                System.out.println("UserDetails username = " + userDetails.getUsername());
//                boolean valid = jwtService.isTokenValid(jwt, userDetails);
//                System.out.println("Token valid = " + valid);
//                if (valid) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities());
//
//                    authentication.setDetails(
//                            new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    System.out.println("Authentication added to SecurityContext");
//                } else {
//                    System.out.println("Token is NOT valid");
//                }
//            }
//        } catch (Exception ex) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//


        // DIAGNOSE
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            System.out.println(
//                    "THREAD=" + Thread.currentThread().getName()
//                            + " | USERNAME=" + username
//                            + " | AUTH_HEADER_PRESENT=" + (authHeader != null)
//            );
//
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            boolean tokenValid = jwtService.isTokenValid(jwt, userDetails);
//
//            System.out.println(
//                    "THREAD=" + Thread.currentThread().getName()
//                            + " | USER_DETAILS_USERNAME=" + userDetails.getUsername()
//                            + " | TOKEN_VALID=" + tokenValid
//            );
//
//            if (tokenValid) {
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities());
//
//                authentication.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                System.out.println(
//                        "THREAD=" + Thread.currentThread().getName()
//                                + " | AUTHENTICATION_SET=true"
//                                + " | AUTH=" + SecurityContextHolder.getContext()
//                                .getAuthentication().getName()
//                );
//
//            } else {
//
//                System.out.println(
//                        "THREAD=" + Thread.currentThread().getName()
//                                + " | TOKEN_VALID=false"
//                );
//            }
//
//        } else {
//
//            System.out.println(
//                    "THREAD=" + Thread.currentThread().getName()
//                            + " | SKIPPED_AUTHENTICATION"
//                            + " | USERNAME=" + username
//                            + " | EXISTING_AUTH="
//                            + (SecurityContextHolder.getContext().getAuthentication() != null)
//            );
//        }

        filterChain.doFilter(request, response);
    }
}