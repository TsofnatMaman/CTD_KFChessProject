package dto;

import board.BoardConfig;
import interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PieceView.
 * Validates the correct scaling and filtering of pieces when converting from game state to view.
 */
class PieceViewTest {

    private IPiece mockPiece;
    private IState mockState;
    private IGraphicsData mockGraphics;
    private IPhysicsData mockPhysics;
    private BoardConfig mockBoardConfig;

    @BeforeEach
    void setup() {
        // Mock a piece, its state, graphics, and physics
        mockPiece = mock(IPiece.class);
        mockState = mock(IState.class);
        mockGraphics = mock(IGraphicsData.class);
        mockPhysics = mock(IPhysicsData.class);
        mockBoardConfig = mock(BoardConfig.class);

        when(mockPiece.getCurrentState()).thenReturn(mockState);
        when(mockState.getGraphics()).thenReturn(mockGraphics);
        when(mockState.getPhysics()).thenReturn(mockPhysics);

        BufferedImage dummyImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        when(mockGraphics.getCurrentFrame()).thenReturn(dummyImage);

        // Mock board config dimensions for scaling
        when(mockBoardConfig.physicsDimension()).thenReturn(new Dimension(100, 200));
        when(mockBoardConfig.panelDimension()).thenReturn(new Dimension(400, 800));
    }

    @Test
    void testFrom_CorrectScaling() {
        // physics coords
        when(mockPhysics.getCurrentX()).thenReturn(50.0);
        when(mockPhysics.getCurrentY()).thenReturn(100.0);

        PieceView view = PieceView.from(mockPiece, mockBoardConfig);

        // Verify that physics coordinates are correctly scaled to panel dimensions
        assertNotNull(view.frame());
        assertEquals(200.0, view.x(), 0.001); // (50 / 100) * 400
        assertEquals(400.0, view.y(), 0.001); // (100 / 200) * 800
    }

    @Test
    void testFrom_ZeroPosition() {
        when(mockPhysics.getCurrentX()).thenReturn(0.0);
        when(mockPhysics.getCurrentY()).thenReturn(0.0);

        PieceView view = PieceView.from(mockPiece, mockBoardConfig);

        // Zero physics coordinates should map to zero panel coordinates
        assertEquals(0.0, view.x());
        assertEquals(0.0, view.y());
    }

    @Test
    void testFrom_MaxPosition() {
        when(mockPhysics.getCurrentX()).thenReturn(100.0);
        when(mockPhysics.getCurrentY()).thenReturn(200.0);

        PieceView view = PieceView.from(mockPiece, mockBoardConfig);

        // Max physics coordinates should map to max panel dimensions
        assertEquals(400.0, view.x());
        assertEquals(800.0, view.y());
    }

    @Test
    void testToPieceViews_FiltersCaptured() {
        // Create captured and alive pieces
        IPiece capturedPiece = mock(IPiece.class);
        when(capturedPiece.isCaptured()).thenReturn(true);

        when(mockPiece.isCaptured()).thenReturn(false);

        IPlayer mockPlayer = mock(IPlayer.class);
        when(mockPlayer.getPieces()).thenReturn(List.of(capturedPiece, mockPiece));

        IBoard mockBoard = mock(IBoard.class);
        when(mockBoard.getPlayers()).thenReturn(new IPlayer[]{mockPlayer});
        when(mockBoard.getBoardConfig()).thenReturn(mockBoardConfig);

        when(mockPhysics.getCurrentX()).thenReturn(10.0);
        when(mockPhysics.getCurrentY()).thenReturn(20.0);

        List<PieceView> result = PieceView.toPieceViews(mockBoard);

        // Only the alive piece should be included
        assertEquals(1, result.size());
        PieceView view = result.get(0);
        assertEquals((10.0 / 100.0) * 400, view.x(), 0.001);
        assertEquals((20.0 / 200.0) * 800, view.y(), 0.001);
    }

    @Test
    void testToPieceViews_MultiplePlayers() {
        // Two pieces for two players, ensure both are converted
        IPiece piece1 = mock(IPiece.class);
        IPiece piece2 = mock(IPiece.class);
        when(piece1.isCaptured()).thenReturn(false);
        when(piece2.isCaptured()).thenReturn(false);

        IState state1 = mock(IState.class);
        IState state2 = mock(IState.class);
        IGraphicsData g1 = mock(IGraphicsData.class);
        IGraphicsData g2 = mock(IGraphicsData.class);
        IPhysicsData p1 = mock(IPhysicsData.class);
        IPhysicsData p2 = mock(IPhysicsData.class);

        when(piece1.getCurrentState()).thenReturn(state1);
        when(piece2.getCurrentState()).thenReturn(state2);
        when(state1.getGraphics()).thenReturn(g1);
        when(state2.getGraphics()).thenReturn(g2);
        when(state1.getPhysics()).thenReturn(p1);
        when(state2.getPhysics()).thenReturn(p2);
        when(g1.getCurrentFrame()).thenReturn(new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB));
        when(g2.getCurrentFrame()).thenReturn(new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB));
        when(p1.getCurrentX()).thenReturn(50.0);
        when(p1.getCurrentY()).thenReturn(100.0);
        when(p2.getCurrentX()).thenReturn(25.0);
        when(p2.getCurrentY()).thenReturn(50.0);

        IPlayer player1 = mock(IPlayer.class);
        IPlayer player2 = mock(IPlayer.class);
        when(player1.getPieces()).thenReturn(List.of(piece1));
        when(player2.getPieces()).thenReturn(List.of(piece2));

        IBoard mockBoard = mock(IBoard.class);
        when(mockBoard.getPlayers()).thenReturn(new IPlayer[]{player1, player2});
        when(mockBoard.getBoardConfig()).thenReturn(mockBoardConfig);

        List<PieceView> result = PieceView.toPieceViews(mockBoard);

        assertEquals(2, result.size());
        // Verify that both pieces are scaled correctly
        assertTrue(result.stream().anyMatch(v -> v.x() == 200.0 && v.y() == 400.0));
        assertTrue(result.stream().anyMatch(v -> v.x() == 100.0 && v.y() == 200.0));
    }
}
