package pieces;

import interfaces.IState;
import moves.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import state.EState;
import state.StateMachine;
import state.PhysicsData;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PieceTest {

    private StateMachine fsmMock;
    private Piece piece;
    private Position startPos;

    @BeforeEach
    void setUp() throws IOException {
        startPos = new Position(0, 0);

        // Create mocks for FSM, State, and Physics
        fsmMock = mock(StateMachine.class);
        IState stateMock = mock(IState.class);
        PhysicsData physicsMock = mock(PhysicsData.class);

        // Setup mock behavior
        when(fsmMock.getCurrentState()).thenReturn(stateMock);
        when(stateMock.isActionFinished(anyLong())).thenReturn(true);
        when(stateMock.getPhysics()).thenReturn(physicsMock);

        // Setup State behavior
        EState stateNameMock = mock(EState.class);
        when(stateMock.getName()).thenReturn(stateNameMock);
        when(stateNameMock.isCanAction()).thenReturn(true);
        when(stateNameMock.isCanCapturable()).thenReturn(true);

        // Setup Physics behavior
        when(physicsMock.getTargetPos()).thenReturn(new Position(2, 2));
        when(physicsMock.getCurrentX()).thenReturn(0.0);
        when(physicsMock.getCurrentY()).thenReturn(0.0);

        // Create the Piece instance
        piece = new PieceStub(EPieceType.P, 1, fsmMock, startPos);
    }

    @Test
    void testUpdateUpdatesPositionWhenActionFinished() {
        // Update should move the piece to target position if action is finished
        piece.update(System.currentTimeMillis());
        assertEquals(2, piece.getPos().getCol());
        assertEquals(2, piece.getPos().getRow());
    }

    @Test
    void testMoveTriggersFsmAndFirstMoveFlag() {
        // Move should call FSM and clear firstMove flag
        Position target = new Position(3, 3);
        piece.move(target);
        verify(fsmMock).onEvent(eq(EPieceEvent.MOVE), eq(startPos), eq(target));
        assertFalse(piece.isFirstMove());
    }

    @Test
    void testJumpTriggersFsm() {
        // Jump should trigger FSM event
        piece.jump();
        verify(fsmMock).onEvent(eq(EPieceEvent.JUMP));
    }

    @Test
    void testMarkCapturedSetsFlag() {
        // Mark piece as captured
        assertFalse(piece.isCaptured());
        piece.markCaptured();
        assertTrue(piece.isCaptured());
    }

    @Test
    void testSetMovesAndGetMoves() {
        // Set and get moves list
        List<Move> dummyMoves = List.of();
        piece.setMoves(dummyMoves);
        assertEquals(dummyMoves, piece.getMoves());
    }

    @Test
    void testCanActionDelegatesToState() {
        // canAction should reflect state
        assertTrue(piece.canAction());
    }

    @Test
    void testCanCapturableDelegatesToState() {
        // canCapturable should reflect state
        assertTrue(piece.isCapturable());
    }

    @Test
    void testGetters() {
        // Basic getter checks
        assertEquals(EPieceType.P, piece.getType());
        assertEquals(1, piece.getPlayer());
        assertEquals(startPos, piece.getPos());
        assertNotNull(piece.getMoves());
    }

    // --- Stub classes ---

    static class PieceStub extends Piece {
        public PieceStub(EPieceType type, int playerId, StateMachine fsm, Position position) throws IOException {
            super(type, playerId, fsm, position);
            setMoves(List.of()); // Prevent IOException from loading Moves
        }
    }
}
