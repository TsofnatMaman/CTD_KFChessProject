package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading configuration and message properties from resource files.
 * Provides safe access with default fallback values.
 */
public final class ConfigLoader {

    private static final Properties config = new Properties();
    private static final Properties messages = new Properties();

    // Prevent instantiation
    private ConfigLoader() {}

    // Static initializer: load properties once
    static {
        loadProperties("config.properties", config);
        loadProperties("messages.properties", messages);
    }

    /**
     * Loads a properties file from the classpath into the given Properties object.
     *
     * @param resourceName name of the resource file
     * @param props        Properties object to load into
     */
    private static void loadProperties(String resourceName, Properties props) {
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in != null) {
                props.load(in);
            } else {
                utils.LogUtils.logDebug("Resource not found: " + resourceName);
            }
        } catch (Exception e) {
            utils.LogUtils.logDebug("Failed to load resource " + resourceName + ": " + e.getMessage());
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
