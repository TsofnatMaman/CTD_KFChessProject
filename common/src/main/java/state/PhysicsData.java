package state;

import board.BoardConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.IPhysicsData;
import pieces.Position;

/**
 * Handles physics data for piece movement, including speed, position, and timing.
 * Manages calculation of piece movement and determines when an action is finished.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicsData implements IPhysicsData {

    /** Speed in meters per second. */
    @JsonProperty("speed_m_per_sec")
    private double speedMetersPerSec;

    /** Duration of action in seconds; -1 means use speed-based calculation. */
    @JsonProperty("action_time")
    private double actionTime;

    /** Current X position in pixels. */
    private double currentX;

    /** Current Y position in pixels. */
    private double currentY;

    /** Starting board position of the piece. */
    private Position startPos;

    /** Target board position of the piece. */
    private Position targetPos;

    /** Board configuration reference. */
    private BoardConfig bc;

    /** Start time of the movement in nanoseconds. */
    private long startTimeNanos;

    /** Default constructor, initializes actionTime to -1. */
    public PhysicsData() {
        actionTime = -1;
    }

    /**
     * Constructs PhysicsData with specified speed.
     * @param speedMetersPerSec Speed in meters per second
     */
    public PhysicsData(double speedMetersPerSec) {
        this.speedMetersPerSec = speedMetersPerSec;
    }

    /** Gets the speed in meters per second. */
    @Override
    public double getSpeedMetersPerSec() {
        return speedMetersPerSec;
    }

    /** Sets the speed in meters per second. */
    @Override
    public void setSpeedMetersPerSec(double speedMetersPerSec) {
        this.speedMetersPerSec = speedMetersPerSec;
    }

    /**
     * Resets the physics data for a new movement.
     * @param state Piece state
     * @param startPos Starting position
     * @param to Target position
     * @param bc Board configuration
     * @param startTimeNanos Start time in nanoseconds
     */
    @Override
    public void reset(EState state, Position startPos, Position to, BoardConfig bc, long startTimeNanos) {
        this.currentX = startPos.getCol() * (bc.physicsDimension().getWidth() / bc.gridDimension().getWidth());
        this.currentY = startPos.getRow() * (bc.physicsDimension().getHeight() / bc.gridDimension().getHeight());
        this.startPos = startPos;
        this.targetPos = to;
        this.bc = bc;
        this.startTimeNanos = startTimeNanos;
    }

    /** Updates the current physics data. */
    @Override
    public void update(long now) {
        updatePosition(now);
    }

    /** Updates current position based on elapsed time and speed. */
    private void updatePosition(long now) {
        double speed = getSpeedMetersPerSec();
        double elapsedSec = (now - startTimeNanos) / 1_000_000_000.0;

        double dx = targetPos.dx(startPos) * (bc.physicsDimension().getWidth() / bc.gridDimension().getWidth());
        double dy = targetPos.dy(startPos) * (bc.physicsDimension().getHeight() / bc.gridDimension().getHeight());
        double totalDistance = Math.sqrt(dx * dx + dy * dy);

        if (totalDistance == 0 || speed == 0) return;

        double distanceSoFar = Math.min(speed * elapsedSec, totalDistance);
        double t = distanceSoFar / totalDistance;

        currentX = (startPos.getCol() * (bc.physicsDimension().getWidth() / bc.gridDimension().getWidth())) + dx * t;
        currentY = (startPos.getRow() * (bc.physicsDimension().getHeight() / bc.gridDimension().getHeight())) + dy * t;
    }

    /** Checks if movement is finished based on speed or actionTime. */
    @Override
    public boolean isActionFinished(long now) {
        if (actionTime != -1) {
            long elapsedNanos = now - startTimeNanos;
            return elapsedNanos >= (long) (actionTime * 1_000_000_000L);
        }

        if (speedMetersPerSec == 0) return false;

        double elapsedSec = (now - startTimeNanos) / 1_000_000_000.0;
        double dx = targetPos.dx(startPos) * (bc.physicsDimension().getWidth() / bc.gridDimension().getWidth());
        double dy = targetPos.dy(startPos) * (bc.physicsDimension().getHeight() / bc.gridDimension().getHeight());
        double totalDistance = Math.sqrt(dx * dx + dy * dy);

        return speedMetersPerSec * elapsedSec >= totalDistance;
    }

    /** Gets current X position in pixels. */
    @Override
    public double getCurrentX() {
        return currentX;
    }

    /** Gets current Y position in pixels. */
    @Override
    public double getCurrentY() {
        return currentY;
    }

    /** Gets the starting position of the piece. */
    @Override
    public Position getStartPos() {
        return startPos;
    }

    /** Gets the target position of the piece. */
    @Override
    public Position getTargetPos() {
        return targetPos;
    }
}
