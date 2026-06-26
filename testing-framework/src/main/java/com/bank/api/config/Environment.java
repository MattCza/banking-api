package com.bank.api.config;

public enum Environment {
    LOCAL("local"),
    DOCKER("docker");

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Environment active() {
        String env = System.getProperty("env", LOCAL.value).toLowerCase();

        for (Environment environment : values()) {
            if (environment.value.equals(env)) {
                return environment;
            }
        }

        throw new IllegalArgumentException("Unsupported environment: " + env);
    }
}
