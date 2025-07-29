package state;

import interfaces.*;
import pieces.Position;

import java.awt.geom.Point2D;

/**
 * Represents the state of a piece, including physics and graphics.
 */
public class State implements IState {
    private EState name;
    private IPhysicsData physics;
    private IGraphicsData graphics;

    private Position startPos;
    private Position targetPos;
    private long startTimeNanos;
    private final double TILE_SIZE;

    /**
     * Constructs a State object representing a piece's state.
     * @param name The state name (EState)
     * @param startPos The starting position
     * @param targetPos The target position
     * @param tileSize The size of a tile
     * @param physics The physics data
     * @param graphics The graphics data
     */
    public State(EState name, Position startPos, Position targetPos,
                 double tileSize, IPhysicsData physics, IGraphicsData graphics) {
        this.name = name;
        this.startPos = startPos;
        this.targetPos = targetPos;
        this.physics = physics;
        this.graphics = graphics;
        this.TILE_SIZE = tileSize;
        reset(EState.IDLE, startPos, null);
    }

    /**
     * Resets the state to a new action, updating physics and graphics.
     * @param state The new state
     * @param from The starting position
     * @param to The target position
     */
    @Override
    public void reset(EState state, Position from, Position to) {
        if (from != null && to != null) {
            this.startPos = new Position(from.getRow(), from.getCol());
            this.targetPos = new Position(to.getRow(), to.getCol());
        }

        this.startTimeNanos = System.nanoTime();

        if (graphics != null) graphics.reset(state, startPos);
        if (physics != null) physics.reset(state, startPos, targetPos, TILE_SIZE, startTimeNanos);
    }

    /**
     * Updates the physics and graphics for the current state.
     */
    @Override
    public void update() {
        if (graphics != null) graphics.update();
        if (physics != null) physics.update();

        if (isActionFinished()) {
            startPos = targetPos;
        }
    }

    /**
     * Checks if the current action (move, jump, rest) is finished.
     * @return true if finished, false otherwise
     */
    @Override
    public boolean isActionFinished() {
        switch (name) {
            case EState.MOVE:
                return physics.isMovementFinished();
            case EState.JUMP:
                return graphics != null && graphics.isAnimationFinished();
            case EState.SHORT_REST:
            case EState.LONG_REST:
                return graphics != null && graphics.isAnimationFinished();
            default:
                // By default, check physics if available, otherwise graphics
                if (physics != null)
                    return physics.isMovementFinished();
                else if (graphics != null)
                    return graphics.isAnimationFinished();
                else
                    return true;
        }
    }

    /**
     * Gets the starting column.
     * @return The starting column index
     */
    @Override
    public int getStartCol() {
        return startPos.getCol();
    }

    /**
     * Gets the starting row.
     * @return The starting row index
     */
    @Override
    public int getStartRow() {
        return startPos.getRow();
    }

    /**
     * Gets the current position in pixels.
     * @return The current position as Point2D.Double
     */
    @Override
    public Point2D.Double getCurrentPosition() {
        return new Point2D.Double(physics.getCurrentX(), physics.getCurrentY());
    }

    /**
     * Gets the target row.
     * @return The target row index
     */
    @Override
    public int getTargetRow() {
        return targetPos.getRow();
    }

    /**
     * Gets the target column.
     * @return The target column index
     */
    @Override
    public int getTargetCol() {
        return targetPos.getCol();
    }

    /**
     * Gets the physics data for the state.
     * @return The physics data
     */
    @Override
    public IPhysicsData getPhysics() {
        return physics;
    }

    /**
     * Gets the graphics data for the state.
     * @return The graphics data
     */
    @Override
    public IGraphicsData getGraphics() {
        return graphics;
    }
}
