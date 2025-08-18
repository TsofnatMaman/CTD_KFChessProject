package state;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.IPhysicsData;
import pieces.Position;

/**
 * Handles physics data for piece movement.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicsData implements IPhysicsData {
    @JsonProperty("speed_m_per_sec")
    private double speedMetersPerSec;

    private double currentX, currentY;
    private Position startPos;
    private Position targetPos;
    private double tileSize;

    private long startTimeNanos;

    public PhysicsData(){}
    /**
     * Constructs PhysicsData for piece movement.
     * @param speedMetersPerSec The speed in meters per second
     */
    public PhysicsData(double speedMetersPerSec) {
        this.speedMetersPerSec = speedMetersPerSec;
    }

    /**
     * Gets the speed in meters per second.
     * @return Speed in meters per second
     */
    @Override
    public double getSpeedMetersPerSec() {
        return speedMetersPerSec;
    }

    /**
     * Sets the speed in meters per second.
     * @param speedMetersPerSec Speed value
     */
    @Override
    public void setSpeedMetersPerSec(double speedMetersPerSec) {
        this.speedMetersPerSec = speedMetersPerSec;
    }

    /**
     * Resets the physics data for a new movement.
     * @param state The state of the piece
     * @param startPos The starting position
     * @param to The target position
     * @param tileSize The size of a tile
     * @param startTimeNanos The start time in nanoseconds
     */
    @Override
    public void reset(EState state, Position startPos, Position to, double tileSize, long startTimeNanos) {
        this.currentX = startPos.getCol() * tileSize;
        this.currentY = startPos.getRow() * tileSize;

        this.startPos = startPos;
        this.targetPos = to;
        this.tileSize = tileSize;
        this.startTimeNanos = startTimeNanos;
    }

    /**
     * Updates the physics data for the piece.
     */
    @Override
    public void update() {
        updatePosition();
    }

    /**
     * Updates the current position based on elapsed time and speed.
     */
    private void updatePosition() {
        double speed = getSpeedMetersPerSec();
        long now = System.nanoTime();
        double elapsedSec = (now - startTimeNanos) / 1_000_000_000.0;

        double dx = targetPos.dx(startPos) * tileSize;
        double dy = targetPos.dy(startPos) * tileSize;

        double totalDistance = Math.sqrt(dx * dx + dy * dy);

        if (totalDistance == 0 || speed == 0) return;

        double distanceSoFar = Math.min(speed * elapsedSec, totalDistance);
        double t = distanceSoFar / totalDistance;

        currentX = (startPos.getCol() * tileSize) + dx * t;
        currentY = (startPos.getRow() * tileSize) + dy * t;
    }

    /**
     * Checks if the movement is finished based on elapsed time and distance.
     * @return true if movement is finished, false otherwise
     */
    @Override
    public boolean isMovementFinished() {
        if (targetPos == null)
            return false;
        double speed = getSpeedMetersPerSec();
        long now = System.nanoTime();
        double elapsedSec = (now - startTimeNanos) / 1_000_000_000.0;

        double dx = targetPos.dx(startPos) * tileSize;
        double dy = targetPos.dy(startPos) * tileSize;
        double totalDistance = Math.sqrt(dx * dx + dy * dy);

        double distanceSoFar = speed * elapsedSec;
        return distanceSoFar >= totalDistance;
    }

    /**
     * Gets the current X position in pixels.
     * @return The X position
     */
    @Override
    public double getCurrentX() {
        return currentX;
    }

    /**
     * Gets the current Y position in pixels.
     * @return The Y position
     */
    @Override
    public double getCurrentY() {
        return currentY;
    }

    @Override
    public Position getStartPos() {
        return startPos;
    }

    @Override
    public Position getTargetPos() {
        return targetPos;
    }
}
