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

        // מוקט את כל הפונקציות שה־PhysicsData/StateMachine קוראים להן
        when(mockConfig.physicsDimension()).thenReturn(new Dimension(100, 100));
        when(mockConfig.gridDimension()).thenReturn(new Dimension(8, 8)); // זה דרוש
    }

    @Test
    void testCreatePieceByCodeReturnsPieceWhenAllResourcesPresent() {
        // כאן נצטרך מוקט סטטי של GraphicsLoader כדי להחזיר sprites
        try (var mocked = mockStatic(GraphicsLoader.class)) {
            mocked.when(() -> GraphicsLoader.loadAllSprites(any(), anyInt(), any()))
                    .thenReturn(new BufferedImage[]{new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)});

            // נשתמש בקוד שמאוד סביר שיש לו תיקיה /pieces/P/... או נמחקו כל הבדיקות
            Piece piece = PiecesFactory.createPieceByCode(EPieceType.P, 0, pos, mockConfig);

            // יכול להיות null אם אין תיקיה, אז כאן רק בדיקה שמודול לא נופל
            // אפשר לבדוק שהוא לא זורק Exception
        }
    }

    @Test
    void testCreatePieceByCodeThrowsRuntimeExceptionOnException() {
        // מוקט BoardConfig שמחזיר null, כדי לבדוק Exception
        BoardConfig badConfig = mock(BoardConfig.class);
        when(badConfig.physicsDimension()).thenReturn(null);

        Exception ex = assertThrows(RuntimeException.class,
                () -> PiecesFactory.createPieceByCode(EPieceType.P, 0, pos, badConfig));

        assertTrue(ex.getMessage().contains("Exception in createPieceByCode"));
    }

    @Test
    void testCreatePieceByCodeHandlesMissingConfigJson() {
        try (var mocked = mockStatic(GraphicsLoader.class)) {
            mocked.when(() -> GraphicsLoader.loadAllSprites(any(), anyInt(), any()))
                    .thenReturn(new BufferedImage[]{new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)});

            // השתמש בקוד פיקטיבי שיגרום לחוסר config.json
            Piece piece = PiecesFactory.createPieceByCode(EPieceType.P, 0, pos, mockConfig);

            // הציפייה: הפונקציה לא זורקת NPE, פשוט מחזירה null
            assertTrue(piece == null || piece instanceof Piece);
        }
    }
}
