package board;

import command.IllegalCmdException;
import interfaces.IBoardEngine;
import interfaces.IPlayer;
import interfaces.IPiece;
import org.junit.jupiter.api.Test;
import pieces.Position;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link Board} covering core behaviours like movement,
 * jumping, legal move retrieval and board updates.
 */
class BoardTest {

    private Board createBoardWithPiece(IBoardEngine engine, IPiece piece, IPlayer player) {
        BoardConfig config = new BoardConfig(new Dimension(8, 8), new Dimension(0, 0), new Dimension(0, 0));
        return new Board(config, engine, new IPlayer[]{player});
    }

    @Test
    void testMoveSetsTargetAndClearsFromPosition() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPiece piece = mock(IPiece.class);
        Position from = new Position(0, 0);
        Position to = new Position(0, 1);
        when(piece.getPos()).thenReturn(from);
        when(piece.getPlayer()).thenReturn(0);

        IPlayer player = mock(IPlayer.class);
        when(player.getPieces()).thenReturn(List.of(piece));
        when(engine.isMoveLegal(any(), any(), any())).thenReturn(true);

        Board board = createBoardWithPiece(engine, piece, player);
        board.move(from, to);

        assertNull(board.getPiece(from));
        assertEquals(0, board.getTarget(to));
        assertTrue(board.hasPieceOrIsTarget(to));
        verify(piece).move(to);
        verify(engine).isMoveLegal(board, from, to);

        board.setIsNoTarget(to);
        assertEquals(board.IS_NO_TARGET, board.getTarget(to));
        assertFalse(board.hasPieceOrIsTarget(to));
    }

    @Test
    void testMoveThrowsWhenIllegal() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPiece piece = mock(IPiece.class);
        Position from = new Position(0, 0);
        Position to = new Position(0, 1);
        when(piece.getPos()).thenReturn(from);
        when(piece.getPlayer()).thenReturn(0);

        IPlayer player = mock(IPlayer.class);
        when(player.getPieces()).thenReturn(List.of(piece));
        when(engine.isMoveLegal(any(), any(), any())).thenReturn(false);

        Board board = createBoardWithPiece(engine, piece, player);
        assertThrows(IllegalCmdException.class, () -> board.move(from, to));
        verify(piece, never()).move(any());
        assertEquals(board.IS_NO_TARGET, board.getTarget(to));
    }

    @Test
    void testJumpCallsPieceWhenLegal() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPiece piece = mock(IPiece.class);
        Position pos = new Position(0, 0);
        when(piece.getPos()).thenReturn(pos);

        IPlayer player = mock(IPlayer.class);
        when(player.getPieces()).thenReturn(List.of(piece));
        when(engine.isJumpLegal(any(), any())).thenReturn(true);

        Board board = createBoardWithPiece(engine, piece, player);
        board.jump(piece);

        verify(engine).isJumpLegal(board, pos);
        verify(piece).jump();
    }

    @Test
    void testJumpThrowsWhenIllegal() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPiece piece = mock(IPiece.class);
        Position pos = new Position(0, 0);
        when(piece.getPos()).thenReturn(pos);

        IPlayer player = mock(IPlayer.class);
        when(player.getPieces()).thenReturn(List.of(piece));
        when(engine.isJumpLegal(any(), any())).thenReturn(false);

        Board board = createBoardWithPiece(engine, piece, player);
        assertThrows(IllegalCmdException.class, () -> board.jump(piece));
        verify(piece, never()).jump();
    }

    @Test
    void testGetLegalMovesDelegatesToEngineWhenInBounds() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPlayer player = mock(IPlayer.class);
        when(player.getPieces()).thenReturn(List.of());
        Board board = createBoardWithPiece(engine, mock(IPiece.class), player);

        Position pos = new Position(0, 0);
        List<Position> expected = List.of(new Position(0, 1));
        when(engine.getLegalMoves(board, pos)).thenReturn(expected);

        List<Position> actual = board.getLegalMoves(pos);
        assertEquals(expected, actual);
        verify(engine).getLegalMoves(board, pos);
    }

    @Test
    void testGetLegalMovesReturnsEmptyForNullOrOutOfBounds() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPlayer player = mock(IPlayer.class);
        when(player.getPieces()).thenReturn(List.of());
        Board board = createBoardWithPiece(engine, mock(IPiece.class), player);

        assertTrue(board.getLegalMoves(null).isEmpty());
        assertTrue(board.getLegalMoves(new Position(-1, 0)).isEmpty());
        verify(engine, never()).getLegalMoves(any(), any());
    }

    @Test
    void testIsInBoundsDelegatesToConfig() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPlayer player = mock(IPlayer.class);
        when(player.getPieces()).thenReturn(List.of());
        Board board = createBoardWithPiece(engine, mock(IPiece.class), player);

        assertTrue(board.isInBounds(new Position(0, 0)));
        assertTrue(board.isInBounds(new Position(7, 7)));
        assertFalse(board.isInBounds(new Position(-1, 0)));
        assertFalse(board.isInBounds(new Position(0, 8)));
    }

    @Test
    void testUpdateAllInvokesEngineAndPieceUpdates() {
        IBoardEngine engine = mock(IBoardEngine.class);
        IPiece piece = mock(IPiece.class);
        Position pos = new Position(0, 0);
        when(piece.getPos()).thenReturn(pos);

        IPlayer player = mock(IPlayer.class);
        List<IPiece> pieces = new ArrayList<>();
        pieces.add(piece);
        when(player.getPieces()).thenReturn(pieces);

        Board board = createBoardWithPiece(engine, piece, player);
        board.updateAll();

        verify(engine).handleUpdatePiece(same(board), eq(player), eq(piece), anyLong());
        verify(piece).update(anyLong());
    }
}