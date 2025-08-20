package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading configuration and message properties from resource files.
 * Provides safe access with default fallback values.
 */
public class ConfigLoader {

    /** Application configuration properties */
    private static final Properties config = new Properties();

    /** Localized message properties */
    private static final Properties messages = new Properties();

    // Static initializer: load properties once
    static {
        // Load config.properties
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                config.load(in);
            }
        } catch (Exception ignored) {}

        // Load messages.properties
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("messages.properties")) {
            if (in != null) {
                messages.load(in);
            }
        } catch (Exception e) {
            LogUtils.logDebug(e.getMessage());
        }
    }

    /**
     * Gets a configuration value with a default fallback.
     *
     * @param key          Property key
     * @param defaultValue Value returned if key not found
     * @return Config value or default
     */
    public static String getConfig(String key, String defaultValue) {
        if (key == null) return defaultValue;
        return config.getProperty(key, defaultValue);
    }

    /**
     * Gets a message value with a default fallback.
     *
     * @param key          Property key
     * @param defaultValue Value returned if key not found
     * @return Message value or default
     */
    public static String getMessage(String key, String defaultValue) {
        if (key == null) return defaultValue;
        return messages.getProperty(key, defaultValue);
    }
}
