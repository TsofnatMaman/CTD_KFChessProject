package state;

import board.BoardConfig;
import interfaces.*;
import pieces.EPieceEvent;
import pieces.Position;

import java.util.Optional;

/**
 * Represents the state of a chess piece, managing physics, graphics, and transitions.
 */
public class State implements IState {

    /** Type of this state (IDLE, MOVE, JUMP, etc.) */
    private final EState name;

    /** Physics data for movement */
    private final IPhysicsData physics;

    /** Graphics data for animations */
    private final IGraphicsData graphics;

    /** Start position for current action */
    private Position startPos;

    /** Target position for current action */
    private Position targetPos;

    /** Board configuration */
    private final BoardConfig bc;

    /**
     * Constructs a State for a chess piece.
     *
     * @param name      State type
     * @param startPos  Starting position
     * @param targetPos Target position
     * @param bc        Board configuration
     * @param physics   Physics data
     * @param graphics  Graphics data
     */
    public State(EState name, Position startPos, Position targetPos,
                 BoardConfig bc, IPhysicsData physics, IGraphicsData graphics) {
        this.name = name;
        this.startPos = startPos;
        this.targetPos = targetPos;
        this.physics = physics;
        this.graphics = graphics;
        this.bc = bc;
    }

    /**
     * Resets the state to a new action, updating physics and graphics.
     *
     * @param from Starting position
     * @param to   Target position
     */
    @Override
    public void reset(Position from, Position to) {
        if (from != null && to != null) {
            this.startPos = from;
            this.targetPos = to;
        }
        long startTimeNanos = System.nanoTime();
        if (graphics != null) graphics.reset();
        if (physics != null) physics.reset(name, startPos, targetPos, bc, startTimeNanos);
    }

    /**
     * Updates the physics and graphics of this state.
     *
     * @param now Current time in nanoseconds
     * @return Optional containing EPieceEvent.DONE if action finished
     */
    @Override
    public Optional<EPieceEvent> update(long now) {
        if (graphics != null) graphics.update(now);
        if (physics != null) physics.update(now);

        if (isActionFinished(now)) {
            startPos = targetPos;
            return Optional.of(EPieceEvent.DONE);
        }

        return Optional.empty();
    }

    /**
     * Checks if the current action is finished.
     *
     * @param now Current time in nanoseconds
     * @return true if finished
     */
    @Override
    public boolean isActionFinished(long now) {
        return physics.isActionFinished(now);
    }

    /** Returns the physics data. */
    @Override
    public IPhysicsData getPhysics() {
        return physics;
    }

    /** Returns the graphics data. */
    @Override
    public IGraphicsData getGraphics() {
        return graphics;
    }

    /** Returns the state name/type. */
    @Override
    public EState getName() {
        return name;
    }
}
