package interfaces;

import board.BoardConfig;
import pieces.Position;
import state.EState;

/**
 * Interface for physics data operations controlling piece movement.
 */
public interface IPhysicsData {

    /**
     * Retrieves the speed of the piece in meters per second.
     * @return Speed in meters per second
     */
    double getSpeedMetersPerSec();

    /**
     * Sets the speed of the piece in meters per second.
     * @param speedMetersPerSec Speed value
     */
    void setSpeedMetersPerSec(double speedMetersPerSec);

    /**
     * Resets the physics data for a new movement.
     * @param state Current state
     * @param startPos Starting board position
     * @param to Target board position
     * @param bc Board configuration
     * @param startTimeNanos Start time in nanoseconds
     */
    void reset(EState state, Position startPos, Position to, BoardConfig bc, long startTimeNanos);

    /**
     * Updates the physics data for the piece based on current time.
     * @param now Current time in nanoseconds
     */
    void update(long now);

    /**
     * Determines if the movement action is finished.
     * @param now Current time in nanoseconds
     * @return True if finished, false otherwise
     */
    boolean isActionFinished(long now);

    /**
     * Gets the current X position in pixels.
     * @return X coordinate
     */
    double getCurrentX();

    /**
     * Gets the current Y position in pixels.
     * @return Y coordinate
     */
    double getCurrentY();

    /**
     * Gets the starting board position for the current movement.
     * @return Starting position
     */
    Position getStartPos();

    /**
     * Gets the target board position for the current movement.
     * @return Target position
     */
    Position getTargetPos();
}
