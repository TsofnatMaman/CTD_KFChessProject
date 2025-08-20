package pieces;

import board.BoardConfig;
import interfaces.IState;
import moves.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import state.EState;
import state.StateMachine;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PieceTest {

    private StateMachine fsmMock;
    private IState stateMock;
    private EState stateNameMock;
    private Position startPos;

    @BeforeEach
    void setUp() {
        fsmMock = mock(StateMachine.class);
        stateMock = mock(IState.class);
        stateNameMock = mock(EState.class);
        startPos = new Position(0, 0);

        // הגדרת Mocks עבור canAction ו-canCapturable
        when(stateMock.getName()).thenReturn(stateNameMock);
        when(stateNameMock.isCanAction()).thenReturn(true);
        when(stateNameMock.isCanCapturable()).thenReturn(true);

        when(fsmMock.getCurrentState()).thenReturn(stateMock);
    }

    @Test
    void testConstructorInitialValues() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 1, fsmMock, startPos);
        assertEquals(EPieceType.P, piece.getType());
        assertEquals(1, piece.getPlayerId());
        assertEquals(startPos, piece.getPosition());
        assertTrue(piece.isFirstMove());
        assertFalse(piece.isCaptured());
        assertNotNull(piece.getMoves());
    }

    @Test
    void testUpdateUpdatesPositionWhenActionFinished() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);
        Position targetPos = new Position(2, 2);

        when(stateMock.isActionFinished()).thenReturn(true);
        when(stateMock.getPhysics()).thenReturn(new StatePhysicsStub(targetPos));

        piece.update(System.currentTimeMillis());

        assertEquals(targetPos, piece.getPosition());
        verify(fsmMock).update(anyLong());
    }

    @Test
    void testUpdateDoesNotChangePositionWhenActionNotFinished() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);

        when(stateMock.isActionFinished()).thenReturn(false);

        piece.update(System.currentTimeMillis());

        assertEquals(startPos, piece.getPosition());
        verify(fsmMock).update(anyLong());
    }

    @Test
    void testMoveCallsFSMAndSetsFirstMove() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);
        Position toPos = new Position(1, 0);

        piece.move(toPos);

        verify(fsmMock).onEvent(eq(EPieceEvent.MOVE), eq(startPos), eq(toPos));
        assertFalse(piece.isFirstMove());
    }

    @Test
    void testJumpCallsFSM() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);

        piece.jump();

        verify(fsmMock).onEvent(EPieceEvent.JUMP);
    }

    @Test
    void testMarkCaptured() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);
        piece.markCaptured();
        assertTrue(piece.isCaptured());
    }

    @Test
    void testSetMovesAndGetMoves() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);
        List<Move> dummyMoves = List.of();
        piece.setMoves(dummyMoves);
        assertEquals(dummyMoves, piece.getMoves());
    }

    @Test
    void testCanCapturableDelegatesToState() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);
        assertTrue(piece.canCapturable());
    }

    @Test
    void testCanActionDelegatesToState() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 0, fsmMock, startPos);
        assertTrue(piece.canAction());
    }

    @Test
    void testGetPosReturnsCurrentPosition() throws IOException {
        Piece piece = new PieceStub(EPieceType.P, 1, fsmMock, startPos);
        assertEquals(startPos, piece.getPos());
    }

    // --- Stubs ---

    static class StatePhysicsStub implements interfaces.IPhysicsData {
        private final Position targetPos;

        StatePhysicsStub(Position targetPos) { this.targetPos = targetPos; }

        @Override public Position getTargetPos() { return targetPos; }

        @Override public double getSpeedMetersPerSec() { return 0; }
        @Override public void setSpeedMetersPerSec(double speedMetersPerSec) { }
        @Override public void reset(EState state, Position startPos, Position to, BoardConfig bc, long startTimeNanos) { }
        @Override public void update(long now) { }
        @Override public boolean isActionFinished() { return false; }
        @Override public double getCurrentX() { return 0; }
        @Override public double getCurrentY() { return 0; }
        @Override public Position getStartPos() { return null; }
    }

    /**
     * Stub של Piece שמונע קריאה אמיתית לקבצי Moves
     */
    static class PieceStub extends Piece {
        public PieceStub(EPieceType type, int playerId, StateMachine fsm, Position position) throws IOException {
            super(type, playerId, fsm, position);
            setMoves(List.of()); // מונע IOException
        }
    }
}
