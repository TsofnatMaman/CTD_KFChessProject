package interfaces;

import board.BoardConfig;
import pieces.Position;
import state.EState;

/**
 * Interface for physics data operations controlling piece movement.
 * Handles piece interpolation, speed, and current position in pixels.
 */
public interface IPhysicsData {

    /** Returns speed of the piece in meters per second. */
    double getSpeedMetersPerSec();

    /** Sets speed of the piece in meters per second. */
    void setSpeedMetersPerSec(double speedMetersPerSec);

    /**
     * Resets physics data for a new movement.
     * Typically called before a move or jump action.
     *
     * @param state Current state of the piece
     * @param startPos Starting board position (grid coordinates)
     * @param to Target board position (grid coordinates)
     * @param bc Board configuration
     * @param startTimeNanos Start time in nanoseconds
     */
    void reset(EState state, Position startPos, Position to, BoardConfig bc, long startTimeNanos);

    /** Updates physics data for the current piece. Should be called every frame. */
    void update(long now);

    /** Returns true if the movement action is finished. */
    boolean isActionFinished(long now);

    /** Gets current X position in pixels (screen coordinates). */
    double getCurrentX();

    /** Gets current Y position in pixels (screen coordinates). */
    double getCurrentY();

    /** Gets starting board position of the current movement. */
    Position getStartPos();

    /** Gets target board position of the current movement. */
    Position getTargetPos();
}
