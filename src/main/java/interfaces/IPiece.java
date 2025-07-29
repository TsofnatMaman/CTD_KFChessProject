package interfaces;

import moves.Move;
import pieces.EPieceType;
import pieces.Position;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

/**
 * Interface for piece operations.
 */
public interface IPiece {

    /**
     * Gets the player index for this piece.
     * @return The player index
     */
    int getPlayer();

    /**
     * Gets the unique ID of the piece.
     * @return The piece ID
     */
    String getId();

    /**
     * Gets the type of the piece.
     * @return The piece type
     */
    EPieceType getType();

    /**
     * Sets the state of the piece.
     * @param newStateName The new state
     */
    void setState(EState newStateName);

    /**
     * Gets the current state object.
     * @return The current state
     */
    IState getCurrentState();

    /**
     * Updates the piece's state.
     */
    void update();

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
     * Gets the current row of the piece.
     * @return The row index
     */
    int getRow();

    /**
     * Gets the current column of the piece.
     * @return The column index
     */
    int getCol();

    /**
     * Gets the current state name.
     * @return The current state name
     */
    EState getCurrentStateName();

    /**
     * Gets the current pixel position of the piece.
     * @return The pixel position
     */
    Point2D.Double getCurrentPixelPosition();

    /**
     * Gets the legal moves for the piece.
     * @return The Moves object
     */
    List<Move> getMoves();

    void setMoves(List<Move> moves);

    /**
     * Gets the map of states for the piece.
     * @return Map of states
     */
    Map<EState, IState> getStates();

    /**
     * Returns true if the piece can move over other pieces.
     * @return true if can move over, false otherwise
     */
    boolean canMoveOver();

    Position getPos();
}
