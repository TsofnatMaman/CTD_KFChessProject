package dto;

import board.BoardConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GameDTOTest {

    @Test
    void testAllArgsConstructor() {
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(400, 400), new Dimension(800, 800));
        PlayerDTO[] players = { new PlayerDTO(1, "Alice", "#FF0000") };

        GameDTO dto = new GameDTO(config, players, 99, 123456789L);

        assertEquals(config, dto.getBoardConfig());
        assertArrayEquals(players, dto.getPlayers());
        assertEquals(99, dto.getYourId());
        assertEquals(123456789L, dto.getStartTimeNano());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        GameDTO dto = new GameDTO();
        BoardConfig config = new BoardConfig(new Dimension(10, 10), new Dimension(100, 100), new Dimension(200, 200));
        PlayerDTO[] players = { new PlayerDTO(2, "Bob", "#0000FF") };

        dto.setBoardConfig(config);
        dto.setPlayers(players);
        dto.setYourId(7);
        dto.setStartTimeNano(42L);

        assertEquals(config, dto.getBoardConfig());
        assertArrayEquals(players, dto.getPlayers());
        assertEquals(7, dto.getYourId());
        assertEquals(42L, dto.getStartTimeNano());
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(400, 400), new Dimension(800, 800));
        PlayerDTO[] players = { new PlayerDTO(5, "Eve", "#00FF00") };

        GameDTO original = new GameDTO(config, players, 11, 999L);
        String json = mapper.writeValueAsString(original);

        GameDTO deserialized = mapper.readValue(json, GameDTO.class);

        assertEquals(original.getYourId(), deserialized.getYourId());
        assertEquals(original.getStartTimeNano(), deserialized.getStartTimeNano());
        assertEquals(original.getBoardConfig().gridDimension(), deserialized.getBoardConfig().gridDimension());
        assertEquals(original.getPlayers()[0].name(), deserialized.getPlayers()[0].name());
    }
}
