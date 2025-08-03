
import board.BoardRulesEngine;
import interfaces.IBoard;
import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BoardRulesEngineTest {

    @Test
    void testIsMoveLegal_validMove_returnsTrue() {
        IBoard board = mock(IBoard.class);
        Position from = new Position(1, 1);
        Position to = new Position(1, 2);

        when(board.isInBounds(from)).thenReturn(true);
        when(board.isInBounds(to)).thenReturn(true);
        when(board.isMoveLegal(from, to)).thenReturn(true);

        assertTrue(BoardRulesEngine.isMoveLegal(board, from, to));
    }

    @Test
    void testIsMoveLegal_outOfBounds_returnsFalse() {
        IBoard board = mock(IBoard.class);
        Position from = new Position(-1, -1);
        Position to = new Position(10, 10);

        when(board.isInBounds(from)).thenReturn(false);
        when(board.isInBounds(to)).thenReturn(false);

        assertFalse(BoardRulesEngine.isMoveLegal(board, from, to));
    }
}