package com.bank.api.data;

public class LoginJsonFactory {
    private LoginJsonFactory() {
    }

    public static String validAdmin() {
        return """
                {
                  "username": "admin",
                  "password": "admin123"
                }
                """;
    }

    public static String malformedJson() {
        return """
                {
                  "username": "admin",
                  "password": "admin123"
                """;
    }

    public static String emptyBody() {
        return "";
    }
}
