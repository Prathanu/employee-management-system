package com.company.ems.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads test configuration from config.properties and Maven -D system properties.
 * Jenkins passes: -Dbase.url=... -Dapi.url=...
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream stream = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (stream != null) {
                PROPERTIES.load(stream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static String getBaseUrl() {
        return get("base.url", "http://localhost").replaceAll("/$", "");
    }

    public static String getApiUrl() {
        return get("api.url", "http://localhost:8080").replaceAll("/$", "");
    }

    public static String getBrowser() {
        return get("browser", "chrome");
    }

    /** Optional explicit browser binary — e.g. C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe */
    public static String getBrowserBinary() {
        String configured = get("browser.binary", "");
        if (!configured.isBlank()) {
            return configured.trim();
        }
        String env = System.getenv("CHROME_BIN");
        return env != null ? env.trim() : "";
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public static int getImplicitWaitSeconds() {
        return Integer.parseInt(get("implicit.wait", "10"));
    }

    public static int getExplicitWaitSeconds() {
        return Integer.parseInt(get("explicit.wait", "15"));
    }

    public static String getUsername() {
        return get("username", "admin");
    }

    public static String getPassword() {
        return get("password", "admin123");
    }
}
