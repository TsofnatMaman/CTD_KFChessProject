import interfaces.IBoard;
import interfaces.IPiece;
import moves.Data;
import moves.ECondition;
import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EConditionTest {

    @Test
    void testNonCapture_conditionTrueWhenTargetIsEmpty() {
        IBoard board = mock(IBoard.class);
        Position to = new Position(2, 2);
        when(board.getPiece(to)).thenReturn(null);

        IPiece piece = mock(IPiece.class);
        Data data = new Data(board, piece, to);
        assertTrue(ECondition.NON_CAPTURE.isCanMove(data));
    }

    @Test
    void testCapture_conditionTrueWhenTargetHasPiece() {
        IBoard board = mock(IBoard.class);
        Position to = new Position(2, 2);
        IPiece target = mock(IPiece.class);
        when(board.getPiece(to)).thenReturn(target);

        IPiece piece = mock(IPiece.class);
        Data data = new Data(board, piece, to);
        assertTrue(ECondition.CAPTURE.isCanMove(data));
    }

    @Test
    void testFirstTime_conditionTrueWhenIdMatchesPosition() {
        IPiece piece = mock(IPiece.class);
        Position pos = new Position(3, 4);
        when(piece.getPos()).thenReturn(pos);
        when(piece.getId()).thenReturn("3,4");

        Data data = new Data(mock(IBoard.class), piece, new Position(4, 4));
        assertTrue(ECondition.FIRST_TIME.isCanMove(data));
    }

    @Test
    void testFirstTime_conditionFalseWhenIdDifferentThanPosition() {
        IPiece piece = mock(IPiece.class);
        Position pos = new Position(3, 4);
        when(piece.getPos()).thenReturn(pos);
        when(piece.getId()).thenReturn("2,2");

        Data data = new Data(mock(IBoard.class), piece, new Position(4, 4));
        assertFalse(ECondition.FIRST_TIME.isCanMove(data));
    }
}