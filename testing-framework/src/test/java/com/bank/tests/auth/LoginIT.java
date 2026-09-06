package com.bank.tests.auth;

import com.bank.api.assertions.AuthAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.ValidationAssertions;
import com.bank.api.client.AuthClient;
import com.bank.api.data.AccountDataFactory;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.data.LoginJsonFactory;
import com.bank.api.dto.request.CreateAccountRequest;
import com.bank.api.dto.request.LoginRequest;
import com.bank.api.dto.response.ErrorResponse;
import com.bank.api.dto.response.LoginResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("functional")
public class LoginIT {

    private final AuthClient authClient = new AuthClient();

    @Test
    @Tag("smoke")
    @DisplayName("Should login successfully when credentials are valid")
    public void shouldLoginSuccessfully_WhenCredentialsAreValid() {
        // Arrange - AAA
        LoginRequest loginRequest = LoginDataFactory.validAdmin();
        // Act
        Response response = authClient.login(loginRequest);

        // Assert
        ResponseAssertions.assertJsonResponse(response, 200);
        LoginResponse loginResponse = response.as(LoginResponse.class);
        AuthAssertions.assertLoggedInAccount(loginResponse);
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when password is invalid")
    public void shouldReturn401_WhenPasswordIsInvalid() {
        LoginRequest loginRequest = LoginDataFactory.invalidPassword();

        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 401);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(error);
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when username is invalid")
    public void shouldReturn401_WhenUsernameIsInvalid() {
        LoginRequest loginRequest = LoginDataFactory.invalidUsername();

        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 401);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(error);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when username is blank")
    public void shouldReturn401_WhenUsernameIsBlank() {
        LoginRequest loginRequest = LoginDataFactory.blankUsername();

        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 400);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(error);
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when username is blank")
    public void shouldReturn401_WhenPasswordIsBlank() {
        LoginRequest loginRequest = LoginDataFactory.blankPassword();

        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 400);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(error);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when username is null")
    public void shouldReturn400_WhenUsernameIsNull() {
        LoginRequest loginRequest = LoginDataFactory.nullUsername();

        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 400);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(error);

        ValidationAssertions.assertFieldErrors(error, "username", "Username is required");
    }

    @Test
    @DisplayName("Should return 400 Bad Request when password is null")
    public void shouldReturn400_WhenPasswordIsNull() {
        LoginRequest loginRequest = LoginDataFactory.nullPassword();

        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 400);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(error);

        ValidationAssertions.assertFieldErrors(error, "password", "Password is required");
    }

    @Test
    @DisplayName("Should return 400 Bad Request when request body contains malformed JSON")
    void shouldReturn400_WhenJsonIsMalformed() {
        String malformedJson = LoginJsonFactory.malformedJson();

        Response response = authClient.login(malformedJson);
        ResponseAssertions.assertJsonResponse(response, 400);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertMalformedJson(error);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when request body is empty")
    void shouldReturn400_WhenJsonIsEmpty() {
        String emptyJson = LoginJsonFactory.emptyBody();

        Response response = authClient.login(emptyJson);
        ResponseAssertions.assertJsonResponse(response, 400);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertMalformedJson(error);
    }

    @Test
    @DisplayName("Should return 415 Unsupported Media Type when Content-Type is missing")
    void shouldReturn415_WhenContentTypeIsMissing() {
        String body = LoginJsonFactory.validAdmin();

        Response response = authClient.loginWithoutContentType(body);
        ResponseAssertions.assertJsonResponse(response, 415);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnsupportedMediaType(error);
    }

    @Test
    @DisplayName("Should return 415 Unsupported Media Type when Content-Type is unsupported")
    void shouldReturn415_WhenContentTypeIsUnsupported() {
        String body = LoginJsonFactory.validAdmin();

        Response response = authClient.loginWithContentType(body, "text/plain");
        ResponseAssertions.assertJsonResponse(response, 415);

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnsupportedMediaType(error);
    }

}
