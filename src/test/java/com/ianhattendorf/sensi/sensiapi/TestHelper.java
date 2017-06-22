package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.exception.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class TestHelper {
    private static final Logger logger = LoggerFactory.getLogger(TestHelper.class);
    private static final String SENSI_API_PROPERTIES_ENV = "SENSI_API_PROPERTIES";
    private static final String SENSI_API_USERNAME_ENV = "SENSI_API_USERNAME";
    private static final String SENSI_API_PASSWORD_ENV = "SENSI_API_PASSWORD";

    public static InputStream loadFile(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    public static boolean isNumeric(String string) {
        try {
            double d = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static ApiCredentials getApiCredentials() {
        if (System.getenv(SENSI_API_USERNAME_ENV) != null) {
            return loadCredentialsFromEnv();
        }
        return loadCredentialsFromFile();
    }

    private static ApiCredentials loadCredentialsFromFile() {
        Properties properties = new Properties();
        String propertiesPath = System.getenv(SENSI_API_PROPERTIES_ENV);
        final Path path;
        if (propertiesPath == null) {
            path = Paths.get(System.getProperty("user.home")).resolve(".sensi-api.properties");
        } else {
            path = Paths.get(propertiesPath);
        }
        logger.info("Getting sensi credentials from file {}", path);
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new APIException("failed to load credentials", e);
        }
        return new ApiCredentials(properties.getProperty("username"), properties.getProperty("password").toCharArray());
    }

    private static ApiCredentials loadCredentialsFromEnv() {
        logger.info("Getting sensi credentials from env");
        return new ApiCredentials(System.getenv(SENSI_API_USERNAME_ENV), System.getenv(SENSI_API_PASSWORD_ENV).toCharArray());
    }

    public static class ApiCredentials {
        private final String username;
        private final char[] password;

        public ApiCredentials(String username, char[] password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public char[] getPassword() {
            return password;
        }
    }
}
