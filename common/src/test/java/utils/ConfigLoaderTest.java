package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @Test
    void testGetConfigWithExistingKey() {
        // קובץ config.properties אמור להכיל איזשהו מפתח ידוע, אם אין – נבדוק שאין חריגה
        String value = ConfigLoader.getConfig("some.existing.key", "defaultValue");
        assertNotNull(value);
    }

    @Test
    void testGetConfigWithMissingKeyReturnsDefault() {
        String result = ConfigLoader.getConfig("non.existing.key", "fallback");
        assertEquals("fallback", result);
    }

    @Test
    void testGetMessageWithExistingKey() {
        // כנ"ל לגבי messages.properties
        String value = ConfigLoader.getMessage("some.existing.message", "defaultMessage");
        assertNotNull(value);
    }

    @Test
    void testGetMessageWithMissingKeyReturnsDefault() {
        String result = ConfigLoader.getMessage("non.existing.message", "fallbackMessage");
        assertEquals("fallbackMessage", result);
    }

    @Test
    void testGetConfigNeverThrowsEvenIfKeyNull() {
        // גם אם המפתח null, צריך להחזיר default
        String result = ConfigLoader.getConfig(null, "defaultValue");
        assertEquals("defaultValue", result);
    }

    @Test
    void testGetMessageNeverThrowsEvenIfKeyNull() {
        String result = ConfigLoader.getMessage(null, "defaultMsg");
        assertEquals("defaultMsg", result);
    }
}
