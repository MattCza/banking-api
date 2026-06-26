package com.bank.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private final Properties properties;

    private Config(Properties properties) {
        this.properties = properties;
    }

    public static Config load() {
        Environment environment = Environment.active();
        String fileName = "env-" + environment.getValue() + ".properties";
        Properties properties = new Properties();

        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IllegalStateException("Properties file " + fileName + " not found");
            }

            properties.load(input);
            return new Config(properties);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration file " + fileName, e);
        }
    }

    public String getBaseUrl() {
        return getRequiredProperty("base.url");
    }

    public int getPort() {
        return Integer.parseInt(getRequiredProperty("port"));
    }

    public String getBasePath() {
        return getRequiredProperty("base.path");
    }

    private String getRequiredProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException(key + " was not found in the active configuration file.");
        }
        return value;
    }
}
