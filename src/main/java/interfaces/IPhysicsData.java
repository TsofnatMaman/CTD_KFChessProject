package interfaces;

import pieces.Position;

/**
 * Interface for physics data operations for piece movement.
 */
public interface IPhysicsData {
    /**
     * Gets the speed in meters per second.
     * @return Speed in meters per second
     */
    double getSpeedMetersPerSec();

    /**
     * Sets the speed in meters per second.
     * @param speedMetersPerSec Speed value
     */
    void setSpeedMetersPerSec(double speedMetersPerSec);

    /**
     * Gets the next state when movement is finished.
     * @return The next state
     */
    EState getNextStateWhenFinished();

    /**
     * Sets the next state when movement is finished.
     * @param nextStateWhenFinished The next state
     */
    void setNextStateWhenFinished(EState nextStateWhenFinished);

    /**
     * Resets the physics data for a new movement.
     * @param state The state
     * @param startPos The starting position
     * @param to The target position
     * @param tileSize The size of a tile
     * @param startTimeNanos The start time in nanoseconds
     */
    void reset(EState state, Position startPos, Position to, double tileSize, long startTimeNanos);

    /**
     * Updates the physics data for the piece.
     */
    void update();

    /**
     * Checks if the movement is finished.
     * @return true if finished, false otherwise
     */
    boolean isMovementFinished();

    /**
     * Gets the current X position in pixels.
     * @return The X position
     */
    double getCurrentX();

    /**
     * Gets the current Y position in pixels.
     * @return The Y position
     */
    double getCurrentY();
}
