package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ConfigLoader utility class.
 * Verifies behavior of getConfig() and getMessage() methods,
 * ensuring default values are returned when keys are missing or null.
 */
class ConfigLoaderTest {

    /**
     * Test that getConfig() returns a non-null value for an existing key.
     * Assumes that "some.existing.key" is present in config.properties.
     */
    @Test
    void testGetConfigWithExistingKey() {
        String value = ConfigLoader.getConfig("some.existing.key", "defaultValue");
        assertNotNull(value, "getConfig should return a non-null value for an existing key");
    }

    /**
     * Test that getConfig() returns the default value when the key does not exist.
     */
    @Test
    void testGetConfigWithMissingKeyReturnsDefault() {
        String result = ConfigLoader.getConfig("non.existing.key", "fallback");
        assertEquals("fallback", result, "getConfig should return the fallback value if key is missing");
    }

    /**
     * Test that getMessage() returns a non-null value for an existing key.
     * Assumes that "some.existing.message" is present in messages.properties.
     */
    @Test
    void testGetMessageWithExistingKey() {
        String value = ConfigLoader.getMessage("some.existing.message", "defaultMessage");
        assertNotNull(value, "getMessage should return a non-null value for an existing key");
    }

    /**
     * Test that getMessage() returns the default value when the key does not exist.
     */
    @Test
    void testGetMessageWithMissingKeyReturnsDefault() {
        String result = ConfigLoader.getMessage("non.existing.message", "fallbackMessage");
        assertEquals("fallbackMessage", result, "getMessage should return the fallback value if key is missing");
    }

    /**
     * Test that getConfig() never throws an exception even if the key is null.
     * Should always return the default value in this case.
     */
    @Test
    void testGetConfigNeverThrowsEvenIfKeyNull() {
        String result = ConfigLoader.getConfig(null, "defaultValue");
        assertEquals("defaultValue", result, "getConfig should return defaultValue when key is null");
    }

    /**
     * Test that getMessage() never throws an exception even if the key is null.
     * Should always return the default value in this case.
     */
    @Test
    void testGetMessageNeverThrowsEvenIfKeyNull() {
        String result = ConfigLoader.getMessage(null, "defaultMsg");
        assertEquals("defaultMsg", result, "getMessage should return defaultMsg when key is null");
    }
}
