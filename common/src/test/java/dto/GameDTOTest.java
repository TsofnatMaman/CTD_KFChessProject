package dto;

import board.BoardConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameDTO.
 * Validates constructors, setters, and JSON serialization.
 */
class GameDTOTest {

    @Test
    void testAllArgsConstructor() {
        // Arrange
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(400, 400), new Dimension(800, 800));
        PlayerDTO[] players = { new PlayerDTO(1, "Alice", "#FF0000") };

        // Act
        GameDTO dto = new GameDTO(config, players, 99, 123456789L);

        // Assert: verify fields initialized correctly
        assertEquals(config, dto.getBoardConfig());
        assertArrayEquals(players, dto.getPlayers());
        assertEquals(99, dto.getYourId());
        assertEquals(123456789L, dto.getStartTimeNano());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        GameDTO dto = new GameDTO();
        BoardConfig config = new BoardConfig(new Dimension(10, 10), new Dimension(100, 100), new Dimension(200, 200));
        PlayerDTO[] players = { new PlayerDTO(2, "Bob", "#0000FF") };

        // Act: set all properties manually
        dto.setBoardConfig(config);
        dto.setPlayers(players);
        dto.setYourId(7);
        dto.setStartTimeNano(42L);

        // Assert: verify setters work
        assertEquals(config, dto.getBoardConfig());
        assertArrayEquals(players, dto.getPlayers());
        assertEquals(7, dto.getYourId());
        assertEquals(42L, dto.getStartTimeNano());
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange: create DTO
        ObjectMapper mapper = new ObjectMapper();
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(400, 400), new Dimension(800, 800));
        PlayerDTO[] players = { new PlayerDTO(5, "Eve", "#00FF00") };

        GameDTO original = new GameDTO(config, players, 11, 999L);

        // Act: serialize to JSON and deserialize back
        String json = mapper.writeValueAsString(original);
        GameDTO deserialized = mapper.readValue(json, GameDTO.class);

        // Assert: verify that fields survive serialization
        assertEquals(original.getYourId(), deserialized.getYourId());
        assertEquals(original.getStartTimeNano(), deserialized.getStartTimeNano());
        assertEquals(original.getBoardConfig().gridDimension(), deserialized.getBoardConfig().gridDimension());
        assertEquals(original.getPlayers()[0].name(), deserialized.getPlayers()[0].name());
    }
}
