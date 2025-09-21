package com.automation.testing.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static Properties properties;
    private static ConfigManager instance;

    private ConfigManager() {
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        String environment = System.getProperty("test.environment", "dev");
        String configFile = environment + "-" + DEFAULT_CONFIG_FILE;

        // Try to load environment-specific config first
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream != null) {
                properties.load(inputStream);
                System.out.println("Loaded configuration from: " + configFile);
            } else {
                // Fall back to default config
                loadDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration from " + configFile + ": " + e.getMessage());
            loadDefaultProperties();
        }
    }

    private void loadDefaultProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
                System.out.println("Loaded default configuration from: " + DEFAULT_CONFIG_FILE);
            } else {
                // Set default values if no config file is found
                setDefaultValues();
            }
        } catch (IOException e) {
            System.err.println("Error loading default configuration: " + e.getMessage());
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        properties.setProperty("base.url", "http://localhost:8080");
        properties.setProperty("request.timeout", "30000");
        properties.setProperty("connection.timeout", "10000");
        properties.setProperty("max.retries", "3");
        properties.setProperty("retry.delay", "1000");
        System.out.println("Using hardcoded default configuration values");
    }

    public String getBaseUrl() {
        return getProperty("base.url");
    }

    public int getRequestTimeout() {
        return Integer.parseInt(getProperty("request.timeout", "30000"));
    }

    public int getConnectionTimeout() {
        return Integer.parseInt(getProperty("connection.timeout", "10000"));
    }

    public int getMaxRetries() {
        return Integer.parseInt(getProperty("max.retries", "3"));
    }

    public int getRetryDelay() {
        return Integer.parseInt(getProperty("retry.delay", "1000"));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer value for property " + key + ": " + value);
            return defaultValue;
        }
    }

    public Properties getAllProperties() {
        return new Properties(properties);
    }
}
