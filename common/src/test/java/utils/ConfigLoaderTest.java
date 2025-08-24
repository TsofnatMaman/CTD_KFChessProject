package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigLoaderTest {

    @Test
    void getConfigReturnsValueFromProperties() {
        String host = ConfigLoader.getConfig("server.host", "default");
        assertEquals("localhost", host);
    }

    @Test
    void getConfigFallsBackToDefaultForMissingOrNullKey() {
        String missing = ConfigLoader.getConfig("does.not.exist", "default");
        String nullKey = ConfigLoader.getConfig(null, "default");
        assertEquals("default", missing);
        assertEquals("default", nullKey);
    }

    @Test
    void getMessageReturnsValueFromProperties() {
        String waitMessage = ConfigLoader.getMessage("wait.message", "default");
        assertEquals("Waiting for second player to join...", waitMessage);
    }

    @Test
    void getMessageFallsBackToDefaultForMissingOrNullKey() {
        String missing = ConfigLoader.getMessage("does.not.exist", "default");
        String nullKey = ConfigLoader.getMessage(null, "default");
        assertEquals("default", missing);
        assertEquals("default", nullKey);
    }
}
