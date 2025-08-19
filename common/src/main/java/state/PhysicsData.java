package state;

import board.BoardConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.IPhysicsData;
import pieces.Position;

/**
 * Handles the physics data for chess piece movement, including speed, position, and timing.
 * This class manages the calculation of piece movement and determines when an action is finished.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicsData implements IPhysicsData {
    @JsonProperty("speed_m_per_sec")
    private double speedMetersPerSec;
    @JsonProperty("action_time")
    private double actionTime;

    private double currentX, currentY;
    private Position startPos;
    private Position targetPos;
    private BoardConfig bc;

    private long startTimeNanos;

    /**
     * Default constructor. Initializes actionTime to -1.
     */
    public PhysicsData(){actionTime = -1;}
    /**
     * Constructs PhysicsData for piece movement.
     *
     * @param speedMetersPerSec The speed in meters per second
     */
    public PhysicsData(double speedMetersPerSec) {
        this.speedMetersPerSec = speedMetersPerSec;
    }

    /**
     * Gets the speed in meters per second.
     *
     * @return Speed in meters per second
     */
    @Override
    public double getSpeedMetersPerSec() {
        return speedMetersPerSec;
    }

    /**
     * Sets the speed in meters per second.
     *
     * @param speedMetersPerSec Speed value
     */
    @Override
    public void setSpeedMetersPerSec(double speedMetersPerSec) {
        this.speedMetersPerSec = speedMetersPerSec;
    }

    /**
     * Resets the physics data for a new movement.
     *
     * @param state          The state of the piece
     * @param startPos       The starting position
     * @param to             The target position
     * @param bc             The board configuration
     * @param startTimeNanos The start time in nanoseconds
     */
    @Override
    public void reset(EState state, Position startPos, Position to, BoardConfig bc, long startTimeNanos) {
        this.currentX = startPos.getCol() * ((double) bc.physicsDimension().getWidth() / bc.gridDimension().getWidth());
        this.currentY = startPos.getRow() * ((double) bc.physicsDimension().getHeight() / bc.gridDimension().getHeight());
        this.startPos = startPos;
        this.targetPos = to;
        this.bc = bc;
        this.startTimeNanos = startTimeNanos;
    }

    /**
     * Updates the physics data for the piece.
     *
     * @param now The current time in nanoseconds
     */
    @Override
    public void update(long now) {
        updatePosition(now);
    }

    /**
     * Updates the current position based on elapsed time and speed.
     *
     * @param now The current time in nanoseconds
     */
    private void updatePosition(long now) {
        double speed = getSpeedMetersPerSec();
        double elapsedSec = (now - startTimeNanos) / 1_000_000_000.0;
        double dx = targetPos.dx(startPos) * ((double) bc.physicsDimension().getWidth() / bc.gridDimension().getWidth());
        double dy = targetPos.dy(startPos) * ((double) bc.physicsDimension().getHeight() / bc.gridDimension().getHeight());
        double totalDistance = Math.sqrt(dx * dx + dy * dy);
        if (totalDistance == 0 || speed == 0) return;
        double distanceSoFar = Math.min(speed * elapsedSec, totalDistance);
        double t = distanceSoFar / totalDistance;
        currentX = (startPos.getCol() * (bc.physicsDimension().getWidth() / bc.gridDimension().getWidth())) + dx * t;
        currentY = (startPos.getRow() * (bc.physicsDimension().getHeight() / bc.gridDimension().getHeight())) + dy * t;
    }

    /**
     * Checks if the movement is finished based on elapsed time and distance.
     *
     * @return true if movement is finished, false otherwise
     */
    @Override
    public boolean isActionFinished() {
        if (actionTime != -1){
            long elapsedNanos = System.nanoTime() - startTimeNanos;
            return elapsedNanos >= (long)(actionTime * 1_000_000_000L);
        }
        if(speedMetersPerSec == 0)
            return false;
        double elapsedSec = (System.nanoTime() - startTimeNanos) / 1_000_000_000.0;
        double dx = targetPos.dx(startPos) * ((double) bc.physicsDimension().getWidth() / bc.gridDimension().getWidth());
        double dy = targetPos.dy(startPos) * ((double) bc.physicsDimension().getHeight() / bc.gridDimension().getHeight());
        double totalDistance = Math.sqrt(dx * dx + dy * dy);
        return speedMetersPerSec * elapsedSec >= totalDistance;
    }

    /**
     * Gets the current X position in pixels.
     *
     * @return The X position
     */
    @Override
    public double getCurrentX() {
        return currentX;
    }

    /**
     * Gets the current Y position in pixels.
     *
     * @return The Y position
     */
    @Override
    public double getCurrentY() {
        return currentY;
    }

    /**
     * Gets the starting position of the piece.
     *
     * @return The starting position
     */
    @Override
    public Position getStartPos() {
        return startPos;
    }

    /**
     * Gets the target position of the piece.
     *
     * @return The target position
     */
    @Override
    public Position getTargetPos() {
        return targetPos;
    }
}
