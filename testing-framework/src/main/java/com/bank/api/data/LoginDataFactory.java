package com.bank.api.data;

import com.bank.api.dto.request.LoginRequest;

public class LoginDataFactory {
    private LoginDataFactory() {
    }

    public static LoginRequest validAdmin() {
        return new LoginRequest("admin", "admin123");
    }

    public static LoginRequest validUser() {
        return new LoginRequest("user", "user123");
    }

    public static LoginRequest invalidPassword() {
        return new LoginRequest("admin", "admin1233");
    }

    public static LoginRequest invalidUsername() {
        return new LoginRequest("admin", "admin123");
    }

    public static LoginRequest blankUsername() {
        return new LoginRequest("", "admin123");
    }

    public static LoginRequest blankPassword() {
        return new LoginRequest("admin", "");
    }

    public static LoginRequest nullUsername() {
        return new LoginRequest(null, "admin123");
    }

    public static LoginRequest nullPassword() { return new LoginRequest("admin1", null);}

}
