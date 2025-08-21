package interfaces;

import pieces.Position;
import java.util.List;

/**
 * Interface defining the rules engine for a board game.
 * Responsible for checking move legality, jumps, and updating piece states.
 */
public interface IBoardEngine {

    /**
     * Determines if moving a piece from one position to another is legal.
     *
     * @param board The game board
     * @param from  Starting position
     * @param to    Target position
     * @return true if the move is legal, false otherwise
     */
    boolean isMoveLegal(IBoard board, Position from, Position to);

    /**
     * Determines if the piece at the given position can perform a jump.
     *
     * @param board The game board
     * @param pos   Position of the piece to check
     * @return true if the jump is legal, false otherwise
     */
    boolean isJumpLegal(IBoard board, Position pos);

    /**
     * Returns a list of all legal moves for the piece at the given position.
     *
     * @param board The game board
     * @param pos   Position of the piece
     * @return List of positions representing legal moves
     */
    List<Position> getLegalMoves(IBoard board, Position pos);

    /**
     * Updates the state of a piece, including movement, animation, and captures.
     *
     * @param board  The game board
     * @param player The player who owns the piece
     * @param piece  The piece to update
     * @param now    Current time in nanoseconds
     */
    void handleUpdatePiece(IBoard board, IPlayer player, IPiece piece, long now);
}
