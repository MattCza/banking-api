package com.bank.controller;

import com.bank.dto.error.ErrorResponse;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DuplicateEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn400WithFieldErrors_WhenValidationFails() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "Email cannot be empty"));
        bindingResult.addError(new FieldError("request", "email", "Email must be a valid format"));
        bindingResult.addError(new FieldError("request", "ownerName", "Owner name must not be blank"));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("BAD_REQUEST");
        assertThat(body.message()).isEqualTo("Validation failed");
        assertThat(body.validationErrors())
                .containsEntry("email", List.of("Email cannot be empty", "Email must be a valid format"))
                .containsEntry("ownerName", List.of("Owner name must not be blank"));
    }

    @Test
    void shouldReturn409_WhenEmailIsDuplicate() {
        DuplicateEmailException ex = new DuplicateEmailException("An account with email 'john@doe.xyz' already exists.");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmailException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponse body = response.getBody();
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.error()).isEqualTo("CONFLICT");
        assertThat(body.message()).isEqualTo(ex.getMessage());
        assertThat(body.validationErrors()).isEmpty();
    }

    @Test
    void shouldReturn404_WhenAccountNotFound() {
        AccountNotFoundException ex = new AccountNotFoundException("Account with id 99 not found.");

        ResponseEntity<ErrorResponse> response = handler.handleAccountNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse body = response.getBody();
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.error()).isEqualTo("NOT_FOUND");
        assertThat(body.message()).isEqualTo(ex.getMessage());
        assertThat(body.validationErrors()).isEmpty();
    }

    @Test
    void shouldReturn400_WhenJsonIsMalformed() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("BAD_REQUEST");
        assertThat(body.message()).isEqualTo("Malformed JSON request");
    }

    @Test
    void shouldReturn415_WhenContentTypeIsUnsupported() {
        HttpMediaTypeNotSupportedException ex = mock(HttpMediaTypeNotSupportedException.class);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMediaTypeNotSupportedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        ErrorResponse body = response.getBody();
        assertThat(body.status()).isEqualTo(415);
        assertThat(body.error()).isEqualTo("UNSUPPORTED_MEDIA_TYPE");
        assertThat(body.message()).isEqualTo("Unsupported Content-Type");
    }

    @Test
    void shouldReturn400_WhenPathParameterIsInvalid() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentTypeMismatchException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("BAD_REQUEST");
        assertThat(body.message()).isEqualTo("Invalid path parameter");
    }

    @Test
    void shouldReturn403_WhenAccessIsDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access is denied");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDeniedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        ErrorResponse body = response.getBody();
        assertThat(body.status()).isEqualTo(403);
        assertThat(body.error()).isEqualTo("FORBIDDEN");
        assertThat(body.message()).isEqualTo("Access denied");
    }
}
