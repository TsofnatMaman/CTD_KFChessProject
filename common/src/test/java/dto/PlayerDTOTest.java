package dto;

import board.BoardConfig;
import interfaces.IPlayer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlayerDTO.
 * Ensures proper conversion between IPlayer and PlayerDTO.
 */
class PlayerDTOTest {

    @Test
    void testDirectCreation() {
        // Directly construct a PlayerDTO and verify fields
        PlayerDTO dto = new PlayerDTO(1, "Alice", "#FF0000");
        assertEquals(1, dto.id());
        assertEquals("Alice", dto.name());
        assertEquals("#FF0000", dto.colorHex());
    }

    @Test
    void testFromIPlayer() {
        // Mock an IPlayer
        IPlayer mockPlayer = Mockito.mock(IPlayer.class);
        Mockito.when(mockPlayer.getId()).thenReturn(2);
        Mockito.when(mockPlayer.getName()).thenReturn("Bob");
        Mockito.when(mockPlayer.getColor()).thenReturn(Color.BLUE);

        // Convert to DTO
        PlayerDTO dto = PlayerDTO.from(mockPlayer);

        assertEquals(2, dto.id());
        assertEquals("Bob", dto.name());
        // Hex color should match the mocked color
        assertTrue("#0000FF".equalsIgnoreCase(dto.colorHex()));
    }

    @Test
    void testToIPlayer() {
        // Create a valid BoardConfig
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(400, 400), new Dimension(800, 800));
        // Create DTO with a valid ID (0 or 1) to prevent PlayerFactory errors
        PlayerDTO dto = new PlayerDTO(0, "Charlie", "#00FF00");

        IPlayer player = PlayerDTO.to(dto, config);

        assertNotNull(player);
        assertEquals(0, player.getId());
        assertEquals("Charlie", player.getName());
    }
}
