package moves;

import interfaces.IBoard;
import interfaces.IPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ECondition enum, which defines movement conditions.
 * Each test verifies whether a move is allowed under a specific condition.
 */
class EConditionTest {

    IBoard boardMock;
    IPiece pieceMock;
    Position targetPos;

    @BeforeEach
    void setup() {
        boardMock = mock(IBoard.class);
        pieceMock = mock(IPiece.class);
        targetPos = new Position(2, 3);
    }

    @Test
    void testNonCapture_whenEmpty() {
        // NON_CAPTURE should allow movement if the target square is empty
        when(boardMock.getPiece(targetPos)).thenReturn(null);
        Data data = new Data(boardMock, pieceMock, targetPos);

        assertTrue(ECondition.NON_CAPTURE.isCanMove(data));
    }

    @Test
    void testNonCapture_whenOccupied() {
        // NON_CAPTURE should disallow movement if the target square is occupied
        IPiece otherPiece = mock(IPiece.class);
        when(boardMock.getPiece(targetPos)).thenReturn(otherPiece);
        Data data = new Data(boardMock, pieceMock, targetPos);

        assertFalse(ECondition.NON_CAPTURE.isCanMove(data));
    }

    @Test
    void testCapture_whenOccupied() {
        // CAPTURE should allow movement if the target square is occupied
        IPiece targetPiece = mock(IPiece.class);
        when(boardMock.getPiece(targetPos)).thenReturn(targetPiece);
        Data data = new Data(boardMock, pieceMock, targetPos);

        assertTrue(ECondition.CAPTURE.isCanMove(data));
    }

    @Test
    void testCapture_whenEmpty() {
        // CAPTURE should disallow movement if the target square is empty
        when(boardMock.getPiece(targetPos)).thenReturn(null);
        Data data = new Data(boardMock, pieceMock, targetPos);

        assertFalse(ECondition.CAPTURE.isCanMove(data));
    }

    @Test
    void testFirstTime_whenFirstMove() {
        // FIRST_TIME condition allows movement if the piece has not moved before
        when(pieceMock.isFirstMove()).thenReturn(true);
        Data data = new Data(boardMock, pieceMock, targetPos);

        assertTrue(ECondition.FIRST_TIME.isCanMove(data));
    }

    @Test
    void testFirstTime_whenNotFirstMove() {
        // FIRST_TIME disallows movement if the piece has already moved
        when(pieceMock.isFirstMove()).thenReturn(false);
        Data data = new Data(boardMock, pieceMock, targetPos);

        assertFalse(ECondition.FIRST_TIME.isCanMove(data));
    }
}
