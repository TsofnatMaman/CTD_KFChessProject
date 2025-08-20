package board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardConfigTest {

    @Test
    void testConstructorAndGetters() {
        Dimension grid = new Dimension(8, 8);
        Dimension panel = new Dimension(400, 400);
        Dimension physics = new Dimension(800, 800);

        BoardConfig config = new BoardConfig(grid, panel, physics);

        assertEquals(grid, config.gridDimension());
        assertEquals(panel, config.panelDimension());
        assertEquals(physics, config.physicsDimension());
    }

    @Test
    void testIsInBounds() {
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(0, 0), new Dimension(0, 0));

        assertTrue(config.isInBounds(0, 0));
        assertTrue(config.isInBounds(7, 7));
        assertFalse(config.isInBounds(-1, 0));
        assertFalse(config.isInBounds(0, -1));
        assertFalse(config.isInBounds(8, 0));
        assertFalse(config.isInBounds(0, 8));
    }

    @Test
    void testGetPlayerOf() {
        assertEquals(0, BoardConfig.getPlayerOf(0));
        assertEquals(0, BoardConfig.getPlayerOf(1));
        assertEquals(1, BoardConfig.getPlayerOf(6));
        assertEquals(1, BoardConfig.getPlayerOf(7));
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(400, 400), new Dimension(800, 800));

        String json = mapper.writeValueAsString(config);
        BoardConfig deserialized = mapper.readValue(json, BoardConfig.class);

        assertEquals(config.gridDimension(), deserialized.gridDimension());
        assertEquals(config.panelDimension(), deserialized.panelDimension());
        assertEquals(config.physicsDimension(), deserialized.physicsDimension());
    }
}
