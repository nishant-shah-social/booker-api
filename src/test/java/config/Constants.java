package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

public final class Constants {
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = Constants.class
                .getClassLoader()
                .getResourceAsStream("config/application.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("application.properties file not found in resources/config");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load application.properties: " + e.getMessage());
        }
    }

    private Constants() {
        // Prevent instantiation
    }

    public static String getBaseUrl() {
        String baseUrl = properties.getProperty("base.url");
        if (baseUrl == null) {
            throw new IllegalStateException("Property 'base.url' not found in application.properties");
        }
        return baseUrl;
    }

    public static String getUsername() {
        String username = properties.getProperty("username");
        if (username == null) {
            throw new IllegalStateException("Property 'username' not found in application.properties");
        }
        return username;
    }

    public static String getPassword() {
        String password = properties.getProperty("password");
        if (password == null) {
            throw new IllegalStateException("Property 'password' not found in application.properties");
        }
        return new String(Base64.getDecoder().decode(password));
    }
}
