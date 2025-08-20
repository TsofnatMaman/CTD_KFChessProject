package interfaces;

import moves.Move;
import pieces.EPieceType;
import pieces.Position;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing a game piece and its operations.
 */
public interface IPiece extends Serializable {

    /**
     * Gets the player index that owns this piece.
     * @return Player index
     */
    int getPlayer();

    /**
     * Gets the type of this piece.
     * @return Piece type
     */
    EPieceType getType();

    /**
     * Updates the piece's internal state and position.
     * @param now Current time in milliseconds
     */
    void update(long now);

    /**
     * Moves the piece to a target position.
     * @param to Target board position
     */
    void move(Position to);

    /**
     * Performs a jump action for this piece.
     */
    void jump();

    /**
     * Checks if this piece has been captured.
     * @return True if captured, false otherwise
     */
    boolean isCaptured();

    /**
     * Marks this piece as captured.
     */
    void markCaptured();

    /**
     * Retrieves the list of legal moves for this piece.
     * @return List of Move objects
     */
    List<Move> getMoves();

    /**
     * Sets the legal moves for this piece.
     * @param moves List of Move objects
     */
    void setMoves(List<Move> moves);

    /**
     * Determines if this piece can capture or move over other pieces.
     * @return True if can capture, false otherwise
     */
    boolean canCapturable();

    /**
     * Gets the current board position of this piece.
     * @return Current position
     */
    Position getPos();

    /**
     * Returns true if this piece has not moved yet.
     * @return True if first move, false otherwise
     */
    boolean isFirstMove();

    /**
     * Gets the current state of this piece in the state machine.
     * @return Current IState object
     */
    IState getCurrentState();

    /**
     * Determines if the piece can perform an action.
     * @return True if can act, false otherwise
     */
    boolean canAction();
}
