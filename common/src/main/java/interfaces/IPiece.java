package interfaces;

import moves.Move;
import pieces.EPieceType;
import pieces.Position;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a game piece and its operations.
 */
public interface IPiece extends Serializable {

    int getPlayer();

    EPieceType getType();

    /**
     * Updates the piece's internal state and position.
     * @param now Current time in milliseconds
     */
    void update(long now);

    /**
     * Moves the piece to a target position on the board (grid coordinates).
     * @param to Target board position
     */
    void move(Position to);

    /** Performs a jump action for this piece. */
    void jump();

    /** Checks if this piece has been captured. */
    boolean isCaptured();

    /** Marks this piece as captured. */
    void markCaptured();

    /** Retrieves the list of legal moves for this piece. */
    List<Move> getMoves();

    /** Sets the legal moves for this piece. */
    void setMoves(List<Move> moves);

    /** Returns true if this piece can be captured by another piece. */
    boolean isCapturable();

    /** Gets the current board position of this piece. */
    Position getPos();

    /** Returns true if this piece has not moved yet. */
    boolean isFirstMove();

    /** Gets the current state of this piece in the state machine. */
    IState getCurrentState();

    /** Returns true if the piece can perform an action this turn. */
    boolean canAction();
}
