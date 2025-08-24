package board;

import board.BoardConfig;
import interfaces.IBoard;
import interfaces.IPiece;
import interfaces.IPlayer;
import interfaces.IState;
import interfaces.IPhysicsData;
import moves.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pieces.EPieceType;
import pieces.Position;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KFCEngineTest {

    private KFCEngine engine;
    private IBoard board;
    private IPiece piece;

    @BeforeEach
    void setUp() {
        engine = new KFCEngine();
        board = mock(IBoard.class);
        piece = mock(IPiece.class);
    }

    @Test
    void testIsMoveLegalAllowsSkippingPiece() {
        Position from = new Position(0, 0);
        Position to = new Position(2, 1);

        when(board.isInBounds(any(Position.class))).thenReturn(true);
        when(board.getPiece(from)).thenReturn(piece);
        when(piece.canAction()).thenReturn(true);
        when(piece.getMoves()).thenReturn(List.of(new Move(2, 1, null)));
        when(piece.getType()).thenReturn(EPieceType.N); // Knight can skip
        when(piece.getPlayer()).thenReturn(0);
        when(board.getPiece(to)).thenReturn(null);
        when(board.getTarget(to)).thenReturn(-1);

        assertTrue(engine.isMoveLegal(board, from, to));
    }

    @Test
    void testIsMoveLegalBlockedPath() {
        Position from = new Position(0, 0);
        Position to = new Position(0, 2);
        Position block = new Position(0, 1);

        when(board.isInBounds(any(Position.class))).thenReturn(true);
        when(board.getPiece(from)).thenReturn(piece);
        when(piece.canAction()).thenReturn(true);
        when(piece.getMoves()).thenReturn(List.of(new Move(0, 2, null)));
        when(piece.getType()).thenReturn(EPieceType.R);
        when(piece.getPlayer()).thenReturn(0);
        when(board.hasPiece(any(Position.class))).thenReturn(false);
        when(board.hasPiece(block)).thenReturn(true); // path blocked
        when(board.getPiece(to)).thenReturn(null);
        when(board.getTarget(to)).thenReturn(-1);

        assertFalse(engine.isMoveLegal(board, from, to));
    }

    @Test
    void testIsMoveLegalCannotCaptureOwnPiece() {
        Position from = new Position(0, 0);
        Position to = new Position(0, 1);
        IPiece own = mock(IPiece.class);

        when(board.isInBounds(any(Position.class))).thenReturn(true);
        when(board.getPiece(from)).thenReturn(piece);
        when(piece.canAction()).thenReturn(true);
        when(piece.getMoves()).thenReturn(List.of(new Move(0, 1, null)));
        when(piece.getType()).thenReturn(EPieceType.R);
        when(piece.getPlayer()).thenReturn(0);
        when(own.getPlayer()).thenReturn(0);
        when(board.getPiece(to)).thenReturn(own);
        when(board.getTarget(to)).thenReturn(-1);

        assertFalse(engine.isMoveLegal(board, from, to));
    }

    @Test
    void testHandleIfPromotionPromotesPawn() throws Exception {
        Position target = new Position(7, 0);
        when(board.getRows()).thenReturn(8);
        BoardConfig config = mock(BoardConfig.class);
        when(board.getBoardConfig()).thenReturn(config);
        when(piece.getType()).thenReturn(EPieceType.P);
        when(piece.getPlayer()).thenReturn(0);
        IPlayer player = mock(IPlayer.class);
        IPiece promoted = mock(IPiece.class);
        when(player.replacePToQ(piece, target, config)).thenReturn(promoted);

        Method m = KFCEngine.class.getDeclaredMethod("handleIfPromotion", IBoard.class, IPlayer.class, IPiece.class, Position.class);
        m.setAccessible(true);
        IPiece result = (IPiece) m.invoke(engine, board, player, piece, target);

        assertSame(promoted, result);
        verify(player).replacePToQ(piece, target, config);
    }

    @Test
    void testGetLegalMovesFiltersUsingIsMoveLegal() {
        KFCEngine spyEngine = Mockito.spy(new KFCEngine());
        Position pos = new Position(0, 0);
        Position t1 = pos.add(1, 0);
        Position t2 = pos.add(0, 1);
        Move m1 = new Move(1, 0, null);
        Move m2 = new Move(0, 1, null);

        when(board.getPiece(pos)).thenReturn(piece);
        when(piece.isCaptured()).thenReturn(false);
        when(piece.getMoves()).thenReturn(List.of(m1, m2));
        doReturn(true).when(spyEngine).isMoveLegal(board, pos, t1);
        doReturn(false).when(spyEngine).isMoveLegal(board, pos, t2);

        List<Position> result = spyEngine.getLegalMoves(board, pos);
        assertEquals(List.of(t1), result);
    }

    @Test
    void testIsJumpLegal() {
        Position pos = new Position(0, 0);
        when(board.getPiece(pos)).thenReturn(piece);
        when(piece.canAction()).thenReturn(true);
        assertTrue(engine.isJumpLegal(board, pos));

        when(board.getPiece(pos)).thenReturn(null);
        assertFalse(engine.isJumpLegal(board, pos));
    }

    @Test
    void testHandleUpdatePieceHandlesCapture() {
        long now = System.nanoTime();
        IPlayer owner = mock(IPlayer.class);
        IPlayer enemy = mock(IPlayer.class);
        when(board.getPlayers()).thenReturn(new IPlayer[]{owner, enemy});

        IState state = mock(IState.class);
        IPhysicsData physics = mock(IPhysicsData.class);
        Position targetPos = new Position(1, 0);

        when(piece.isCaptured()).thenReturn(false);
        when(piece.getCurrentState()).thenReturn(state);
        when(state.isActionFinished(anyLong())).thenReturn(true);
        when(state.getPhysics()).thenReturn(physics);
        when(physics.getTargetPos()).thenReturn(targetPos);
        when(piece.getPlayer()).thenReturn(0);
        when(piece.getType()).thenReturn(EPieceType.R);

        IPiece target = mock(IPiece.class);
        when(target.isCaptured()).thenReturn(false);
        when(target.isCapturable()).thenReturn(true);
        when(target.getPlayer()).thenReturn(1);
        when(board.getPiece(targetPos)).thenReturn(target);

        engine.handleUpdatePiece(board, owner, piece, now);

        verify(enemy).markPieceCaptured(target);
        verify(board).setGrid(targetPos, piece);
        verify(board).setIsNoTarget(targetPos);
    }

    @Test
    void testHandleUpdatePieceHandlesPromotion() {
        long now = System.nanoTime();
        IPlayer owner = mock(IPlayer.class);

        IState state = mock(IState.class);
        IPhysicsData physics = mock(IPhysicsData.class);
        Position targetPos = new Position(7, 0);

        when(piece.isCaptured()).thenReturn(false);
        when(piece.getCurrentState()).thenReturn(state);
        when(state.isActionFinished(anyLong())).thenReturn(true);
        when(state.getPhysics()).thenReturn(physics);
        when(physics.getTargetPos()).thenReturn(targetPos);
        when(piece.getPlayer()).thenReturn(0);
        when(piece.getType()).thenReturn(EPieceType.P);
        when(board.getPiece(targetPos)).thenReturn(null);

        when(board.getRows()).thenReturn(8);
        BoardConfig config = mock(BoardConfig.class);
        when(board.getBoardConfig()).thenReturn(config);
        IPiece promoted = mock(IPiece.class);
        when(owner.replacePToQ(piece, targetPos, config)).thenReturn(promoted);

        engine.handleUpdatePiece(board, owner, piece, now);

        verify(owner).replacePToQ(piece, targetPos, config);
        verify(board).setGrid(targetPos, promoted);
        verify(board).setIsNoTarget(targetPos);
    }
}