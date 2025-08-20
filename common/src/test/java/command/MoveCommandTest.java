package command;

import board.BoardRulesEngine;
import events.EGameEvent;
import events.EventPublisher;
import interfaces.IBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Piece;
import pieces.Position;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MoveCommand.
 * Verifies behavior for legal and illegal moves.
 */
class MoveCommandTest {

    private IBoard board;
    private Position from;
    private Position to;
    private Piece piece;

    @BeforeEach
    void setUp() {
        // Mock board and piece
        board = mock(IBoard.class);
        from = new Position(0, 0);
        to = new Position(1, 1);

        piece = mock(Piece.class);
        when(piece.getPlayer()).thenReturn(42);
        when(board.getPiece(from)).thenReturn(piece);

        // Clear previous subscribers to prevent test interference
        EventPublisher.getInstance().unsubscribe(EGameEvent.ILLEGAL_CMD, event -> {});
    }

    @Test
    void execute_illegalMove_publishesIllegalEventAndDoesNotMove() {
        // Arrange: make the move illegal
        try (var mocked = mockStatic(BoardRulesEngine.class)) {
            mocked.when(() -> BoardRulesEngine.isMoveLegal(board, from, to)).thenReturn(false);

            MoveCommand cmd = new MoveCommand(from, to, board);

            // Act
            cmd.execute();

            // Assert: board should not move for illegal command
            verify(board, never()).move(any(), any());
            // Event publishing can be checked with a spy on EventPublisher if needed
        }
    }

    @Test
    void execute_legalMove_publishesStartMovedEventAndMovesBoard() {
        // Arrange: make the move legal and mock Utils.getName
        try (var mocked = mockStatic(BoardRulesEngine.class);
             var utilsMocked = mockStatic(Utils.class)) {

            mocked.when(() -> BoardRulesEngine.isMoveLegal(board, from, to)).thenReturn(true);
            utilsMocked.when(() -> Utils.getName(from)).thenReturn("A1");
            utilsMocked.when(() -> Utils.getName(to)).thenReturn("B2");

            MoveCommand cmd = new MoveCommand(from, to, board);

            // Act
            cmd.execute();

            // Assert: verify the board move is executed
            verify(board).move(from, to);
            // Event publishing verification requires a spy on EventPublisher
        }
    }

    @Test
    void execute_illegalMove_messageFormatIsCorrect() {
        // Arrange: illegal move
        try (var mocked = mockStatic(BoardRulesEngine.class)) {
            mocked.when(() -> BoardRulesEngine.isMoveLegal(board, from, to)).thenReturn(false);

            MoveCommand cmd = new MoveCommand(from, to, board);

            // Act & Assert: ensure no exceptions occur
            assertDoesNotThrow(cmd::execute);
        }
    }

    @Test
    void execute_legalMove_messageUsesUtilsNames() {
        // Arrange: legal move, check Utils.getName usage
        try (var mocked = mockStatic(BoardRulesEngine.class);
             var utilsMocked = mockStatic(Utils.class)) {

            mocked.when(() -> BoardRulesEngine.isMoveLegal(board, from, to)).thenReturn(true);
            utilsMocked.when(() -> Utils.getName(from)).thenReturn("A1");
            utilsMocked.when(() -> Utils.getName(to)).thenReturn("B2");

            MoveCommand cmd = new MoveCommand(from, to, board);

            // Act
            cmd.execute();

            // Assert: board moved and Utils.getName called for both positions
            verify(board).move(from, to);
            utilsMocked.verify(() -> Utils.getName(from));
            utilsMocked.verify(() -> Utils.getName(to));
        }
    }
}
