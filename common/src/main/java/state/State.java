package state;

import interfaces.*;
import pieces.EPieceEvent;
import pieces.Position;

import java.awt.geom.Point2D;
import java.util.Optional;

/**
 * Represents the state of a piece, including physics and graphics.
 */
public class State implements IState {
    private final EState name;
    private final IPhysicsData physics;
    private final IGraphicsData graphics;

    private Position startPos;
    private Position targetPos;
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
    }

    /**
     * Resets the state to a new action, updating physics and graphics.
     * @param from The starting position
     * @param to The target position
     */
    @Override
    public void reset(Position from, Position to) {
        if (from != null && to != null) {
            this.startPos = from;//TODO:maybe copy
            this.targetPos = to;
        }

        long startTimeNanos = System.nanoTime();

        if (graphics != null) graphics.reset(name, startPos);
        if (physics != null) physics.reset(name, startPos, targetPos, TILE_SIZE, startTimeNanos);
    }

    /**
     * Updates the physics and graphics for the current state.
     */
    @Override
    public Optional<EPieceEvent> update() {
        if (graphics != null) graphics.update();
        if (physics != null) physics.update();

        if (isActionFinished()) {
            startPos = targetPos;
            return Optional.of(EPieceEvent.DONE);
        }

        return Optional.empty();
    }

    /**
     * Checks if the current action (move, jump, rest) is finished.
     * @return true if finished, false otherwise
     */
    @Override
    public boolean isActionFinished() {
        switch (name) {
            case MOVE:
                return physics.isMovementFinished();
            case JUMP:
                return graphics != null && graphics.isAnimationFinished();
            case SHORT_REST:
            case LONG_REST:
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
     * Gets the current position in pixels.
     * @return The current position as Point2D.Double
     */
    @Override
    public Point2D.Double getCurrentPosition() {
        return new Point2D.Double(physics.getCurrentX(), physics.getCurrentY());
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

    @Override
    public EState getName() {
        return name;
    }
}
