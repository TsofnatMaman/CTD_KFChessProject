package board;

import interfaces.IBoard;
import pieces.Position;

/**
 * Helper class to check legality of moves and jumps on a board.
 */
public class BoardRulesEngine {

    /**
     * Checks if a move from one position to another is legal on the given board.
     *
     * @param board the game board
     * @param from starting position
     * @param to destination position
     * @return true if the move is legal, false otherwise
     */
    public static boolean isMoveLegal(IBoard board, Position from, Position to) {
        return board.isInBounds(from) && board.isInBounds(to) && board.isMoveLegal(from, to);
    }

    /**
     * Checks if a jump action is legal for the piece at the given position.
     *
     * @param board the game board
     * @param pos position of the piece
     * @return true if the jump is legal, false otherwise
     */
    public static boolean isJumpLegal(IBoard board, Position pos) {
        return board.isInBounds(pos) && board.isJumpLegal(board.getPiece(pos));
    }
}
