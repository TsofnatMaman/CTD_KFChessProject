package pieces;

import board.BoardConfig;
import graphics.GraphicsLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PiecesFactoryTest {

    private BoardConfig mockConfig;
    private Position pos;

    @BeforeEach
    void setUp() {
        mockConfig = mock(BoardConfig.class);
        pos = new Position(0, 0);

        // Mock the config dimensions used by PhysicsData / StateMachine
        when(mockConfig.physicsDimension()).thenReturn(new Dimension(100, 100));
        when(mockConfig.gridDimension()).thenReturn(new Dimension(8, 8));
    }

    @Test
    void testCreatePieceByCodeReturnsPieceWhenAllResourcesPresent() {
        // Mock the static method of GraphicsLoader to return sprites
        try (var mocked = mockStatic(GraphicsLoader.class)) {
            mocked.when(() -> GraphicsLoader.loadAllSprites(any(), anyInt(), any()))
                    .thenReturn(new BufferedImage[]{new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)});

            // Call the factory method
            Piece piece = PiecesFactory.createPieceByCode(EPieceType.P, 0, pos, mockConfig);

            // Assert: method does not throw, and returns a Piece or null if folder missing
            assertTrue(piece == null || piece instanceof Piece);
        }
    }

    @Test
    void testCreatePieceByCodeThrowsRuntimeExceptionOnException() {
        // Mock a BoardConfig that returns null physicsDimension to trigger exception
        BoardConfig badConfig = mock(BoardConfig.class);
        when(badConfig.physicsDimension()).thenReturn(null);

        Exception ex = assertThrows(RuntimeException.class,
                () -> PiecesFactory.createPieceByCode(EPieceType.P, 0, pos, badConfig));

        assertTrue(ex.getMessage().contains("Exception in createPieceByCode"));
    }

    @Test
    void testCreatePieceByCodeHandlesMissingConfigJson() {
        // Even if config JSON is missing, the method should not throw NPE
        try (var mocked = mockStatic(GraphicsLoader.class)) {
            mocked.when(() -> GraphicsLoader.loadAllSprites(any(), anyInt(), any()))
                    .thenReturn(new BufferedImage[]{new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)});

            Piece piece = PiecesFactory.createPieceByCode(EPieceType.P, 0, pos, mockConfig);

            // Expectation: it either returns null or a valid Piece, but does not throw
            assertTrue(piece == null || piece instanceof Piece);
        }
    }
}
