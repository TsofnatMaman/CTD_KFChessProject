import org.junit.jupiter.api.Test;
import utils.ConfigLoader;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    void testGetConfig_existingKey_returnsValue() {
        String value = ConfigLoader.getConfig("board.rows", "-1");
        assertNotEquals("-1", value);
    }

    @Test
    void testGetConfig_missingKey_returnsDefault() {
        String value = ConfigLoader.getConfig("non.existing.key", "default");
        assertEquals("default", value);
    }
}