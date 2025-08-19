package interfaces;

import moves.Move;
import pieces.EPieceType;
import pieces.Position;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;

/**
 * Interface for piece operations.
 */
public interface IPiece extends Serializable {

    /**
     * Gets the player index for this piece.
     * @return The player index
     */
    int getPlayer();

    /**
     * Gets the type of the piece.
     * @return The piece type
     */
    EPieceType getType();

    /**
     * Updates the piece's state.
     */
    void update(long now);

    /**
     * Moves the piece to a new position.
     * @param to The target position
     */
    void move(Position to);

    /**
     * Performs a jump action for the piece.
     */
    void jump();

    /**
     * Returns true if the piece is captured.
     * @return true if captured, false otherwise
     */
    boolean isCaptured();

    /**
     * Marks the piece as captured.
     */
    void markCaptured();

    /**
     * Gets the legal moves for the piece.
     * @return The Moves object
     */
    List<Move> getMoves();

    void setMoves(List<Move> moves);

    /**
     * Returns true if the piece can move over other pieces.
     * @return true if can move over, false otherwise
     */
    boolean canCapturable();

    Position getPos();

    boolean isFirstMove();

    void setFirstMove(boolean firstMove);

    IState getCurrentState();

    boolean canAction();
}
