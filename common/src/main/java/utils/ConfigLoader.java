package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Central utility for loading configuration and message properties with safe defaults.
 */
public class ConfigLoader {
    private static final Properties config = new Properties();
    private static final Properties messages = new Properties();

    static {
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) config.load(in);
        } catch (Exception ignored) {}
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("messages.properties")) {
            if (in != null) messages.load(in);
        } catch (Exception ignored) {}
    }

    /**
     * Gets a config value with fallback default.
     */
    public static String getConfig(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    /**
     * Gets a message value with fallback default.
     */
    public static String getMessage(String key, String defaultValue) {
        return messages.getProperty(key, defaultValue);
    }
}
