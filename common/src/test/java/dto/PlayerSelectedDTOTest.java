package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;

class PlayerSelectedDTOTest {

    @Test
    void testCreationAndGetters() {
        Position pos = new Position(3, 5);
        PlayerSelectedDTO dto = new PlayerSelectedDTO(1, pos);

        assertEquals(1, dto.playerId());
        assertEquals(pos, dto.selection());
    }

    @Test
    void testEqualsAndHashCode() {
        Position pos1 = new Position(2, 4);
        Position pos2 = new Position(2, 4);

        PlayerSelectedDTO dto1 = new PlayerSelectedDTO(0, pos1);
        PlayerSelectedDTO dto2 = new PlayerSelectedDTO(0, pos2);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        Position pos = new Position(6, 7);
        PlayerSelectedDTO dto = new PlayerSelectedDTO(2, pos);

        String str = dto.toString();
        assertTrue(str.contains("playerId=2"));
        assertTrue(str.contains("selection="));
    }

    @Test
    void testNullSelection() {
        PlayerSelectedDTO dto = new PlayerSelectedDTO(3, null);
        assertEquals(3, dto.playerId());
        assertNull(dto.selection());
    }

    @Test
    void testJsonSerializationAndDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Position pos = new Position(4, 1);
        PlayerSelectedDTO original = new PlayerSelectedDTO(9, pos);

        String json = mapper.writeValueAsString(original);
        PlayerSelectedDTO deserialized = mapper.readValue(json, PlayerSelectedDTO.class);

        assertEquals(original, deserialized);
        assertEquals(9, deserialized.playerId());
        assertEquals(pos, deserialized.selection());
    }
}
