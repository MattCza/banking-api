package com.bank.tests.auth;

import com.bank.api.assertions.AuthAssertions;
import com.bank.api.assertions.ErrorAssertions;
import com.bank.api.assertions.ResponseAssertions;
import com.bank.api.assertions.ValidationAssertions;
import com.bank.api.client.AuthClient;
import com.bank.api.data.LoginDataFactory;
import com.bank.api.data.LoginJsonFactory;
import com.bank.api.dto.request.LoginRequest;
import com.bank.api.dto.response.ErrorResponse;
import com.bank.api.dto.response.LoginResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Tag("functional")
public class LoginIT {

    private final AuthClient authClient = new AuthClient();

    @Test
    @Tag("smoke")
    @DisplayName("Should login successfully when credentials are valid")
    void shouldLoginSuccessfully_WhenCredentialsAreValid() {
        LoginRequest loginRequest = LoginDataFactory.validAdmin();
        Response response = authClient.login(loginRequest);

        ResponseAssertions.assertJsonResponse(response, 200);
        ResponseAssertions.assertMatchesSchema(response, "schemas/login-response-schema.json");
        LoginResponse loginResponse = response.as(LoginResponse.class);
        AuthAssertions.assertLoggedInAccount(loginResponse);
    }

    private static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of("invalid password", LoginDataFactory.invalidPassword()),
                Arguments.of("invalid username", LoginDataFactory.invalidUsername())
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidCredentials")
    @DisplayName("Should return 401 Unauthorized when credentials are invalid")
    void shouldReturn401_WhenCredentialsAreInvalid(String description, LoginRequest loginRequest) {
        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 401);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertUnauthorized(error);
    }

    private static Stream<Arguments> blankCredentials() {
        return Stream.of(
                Arguments.of("blank username", LoginDataFactory.blankUsername()),
                Arguments.of("blank password", LoginDataFactory.blankPassword())
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("blankCredentials")
    @DisplayName("Should return 400 Bad Request when a credential is blank")
    void shouldReturn400_WhenCredentialIsBlank(String description, LoginRequest loginRequest) {
        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 400);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(error);
    }

    private static Stream<Arguments> nullCredentials() {
        return Stream.of(
                Arguments.of("null username", LoginDataFactory.nullUsername(), "username", "Username is required"),
                Arguments.of("null password", LoginDataFactory.nullPassword(), "password", "Password is required")
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("nullCredentials")
    @DisplayName("Should return 400 Bad Request when a credential is null")
    void shouldReturn400_WhenCredentialIsNull(String description, LoginRequest loginRequest, String field, String message) {
        Response response = authClient.login(loginRequest);
        ResponseAssertions.assertJsonResponse(response, 400);
        ResponseAssertions.assertMatchesSchema(response, "schemas/error-response-schema.json");

        ErrorResponse error = response.as(ErrorResponse.class);
        ErrorAssertions.assertBadRequest(error);

        ValidationAssertions.assertFieldErrors(error, field, message);
    }

    private static Stream<Arguments> malformedBodies() {
        return Stream.of(
                Arguments.of("malformed JSON", LoginJsonFactory.malformedJson()),
                Arguments.of("empty body", LoginJsonFactory.emptyBody())
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("malformedBodies")
    @DisplayName("Should return 400 Bad Request when request body is malformed")
    void shouldReturn400_WhenBodyIsMalformed(String description, String body) {
        Response response = authClient.login(body);
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
