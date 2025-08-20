package board;

import interfaces.IBoard;
import interfaces.IPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BoardRulesEngine helper methods.
 */
class BoardRulesEngineTest {

    private IBoard board;
    private IPiece dummyPiece;

    @BeforeEach
    void setUp() {
        // Stub IPiece implementation for testing
        dummyPiece = new IPiece() {
            @Override public int getPlayer() { return 0; }
            @Override public pieces.EPieceType getType() { return null; }
            @Override public void update(long now) {}
            @Override public void move(Position to) {}
            @Override public void jump() {}
            @Override public boolean isCaptured() { return false; }
            @Override public void markCaptured() {}
            @Override public java.util.List<moves.Move> getMoves() { return java.util.List.of(); }
            @Override public void setMoves(java.util.List<moves.Move> moves) {}
            @Override public boolean canCapturable() { return true; }
            @Override public Position getPos() { return new Position(0,0); }
            @Override public boolean isFirstMove() { return false; }
            @Override public interfaces.IState getCurrentState() { return null; }
            @Override public boolean canAction() { return true; }
        };

        // Stub IBoard implementation for testing move and jump rules
        board = new IBoard() {
            @Override public boolean hasPiece(Position pos) { return false; }
            @Override public boolean hasPieceOrIsTarget(Position pos) { return false; }
            @Override public IPiece getPiece(Position pos) { return dummyPiece; }
            @Override public int getPlayerOf(Position pos) { return 0; }
            @Override public void move(Position from, Position to) {}
            @Override public void updateAll() {}
            @Override public boolean isInBounds(Position p) {
                return p.getRow() >= 0 && p.getCol() >= 0 && p.getRow() < 8 && p.getCol() < 8;
            }
            @Override public boolean isMoveLegal(Position from, Position to) {
                // Only moves one column to the right are legal
                return (to.getRow() == from.getRow()) && (to.getCol() == from.getCol() + 1);
            }
            @Override public boolean isJumpLegal(IPiece p) { return true; }
            @Override public void jump(IPiece p) {}
            @Override public interfaces.IPlayer[] getPlayers() { return new interfaces.IPlayer[0]; }
            @Override public int getRows() { return 8; }
            @Override public int getCols() { return 8; }
            @Override public board.BoardConfig getBoardConfig() { return null; }
            @Override public java.util.List<Position> getLegalMoves(Position selectedPosition) { return java.util.List.of(); }
        };
    }

    @Test
    void testMoveLegalInsideBoundsAndAllowed() {
        Position from = new Position(3, 3);
        Position to = new Position(3, 4); // Legal according to stub
        assertTrue(BoardRulesEngine.isMoveLegal(board, from, to));
    }

    @Test
    void testMoveIllegalOutOfBounds() {
        Position from = new Position(3, 3);
        Position to = new Position(9, 9); // Out of board bounds
        assertFalse(BoardRulesEngine.isMoveLegal(board, from, to));
    }

    @Test
    void testMoveIllegalNotAllowedByBoard() {
        Position from = new Position(3, 3);
        Position to = new Position(4, 4); // Not allowed by stub rules
        assertFalse(BoardRulesEngine.isMoveLegal(board, from, to));
    }

    @Test
    void testJumpLegalWhenInBoundsAndBoardAllows() {
        Position pos = new Position(0, 0);
        assertTrue(BoardRulesEngine.isJumpLegal(board, pos));
    }

    @Test
    void testJumpIllegalWhenOutOfBounds() {
        Position pos = new Position(-1, 0); // Out of board
        assertFalse(BoardRulesEngine.isJumpLegal(board, pos));
    }

    @Test
    void testJumpIllegalWhenBoardDisallows() {
        // Custom board disallowing jumps
        IBoard customBoard = new IBoard() {
            @Override public boolean isInBounds(Position p) { return true; }
            @Override public boolean isJumpLegal(IPiece p) { return false; }
            @Override public boolean hasPiece(Position pos) { return false; }
            @Override public boolean hasPieceOrIsTarget(Position pos) { return false; }
            @Override public IPiece getPiece(Position pos) { return dummyPiece; }
            @Override public int getPlayerOf(Position pos) { return 0; }
            @Override public void move(Position from, Position to) {}
            @Override public void updateAll() {}
            @Override public boolean isMoveLegal(Position from, Position to) { return false; }
            @Override public void jump(IPiece p) {}
            @Override public interfaces.IPlayer[] getPlayers() { return new interfaces.IPlayer[0]; }
            @Override public int getRows() { return 8; }
            @Override public int getCols() { return 8; }
            @Override public board.BoardConfig getBoardConfig() { return null; }
            @Override public java.util.List<Position> getLegalMoves(Position selectedPosition) { return java.util.List.of(); }
        };

        Position pos = new Position(2, 2);
        assertFalse(BoardRulesEngine.isJumpLegal(customBoard, pos));
    }
}
