package state;

import board.BoardConfig;
import interfaces.*;
import pieces.EPieceEvent;
import pieces.Position;

import java.util.Optional;

/**
 * Represents the state of a chess piece, including its physics and graphics data.
 * This class manages the state transitions, updates, and provides access to the
 * current physics and graphics information for a piece.
 */
public class State implements IState {
    private final EState name;
    private final IPhysicsData physics;
    private final IGraphicsData graphics;

    private Position startPos;
    private Position targetPos;
    private final BoardConfig bc;


    /**
     * Constructs a new State for a chess piece.
     *
     * @param name      The state name (enum)
     * @param startPos  The starting position
     * @param targetPos The target position
     * @param bc        The board configuration
     * @param physics   The physics data object
     * @param graphics  The graphics data object
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
     * Resets the state to a new action, updating physics and graphics data.
     *
     * @param from The starting position
     * @param to   The target position
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
     * Updates the physics and graphics for the current state.
     *
     * @param now The current time in nanoseconds
     * @return An Optional containing EPieceEvent.DONE if the action is finished, otherwise empty
     */
    @Override
    public Optional<EPieceEvent> update(long now) {
        if (graphics != null) graphics.update(now);
        if (physics != null) physics.update(now);
        if (isActionFinished()) {
            startPos = targetPos;
            return Optional.of(EPieceEvent.DONE);
        }
        return Optional.empty();
    }

    /**
     * Checks if the current action (move, jump, rest) is finished.
     *
     * @return true if finished, false otherwise
     */
    @Override
    public boolean isActionFinished() {
        return physics.isActionFinished();
    }

    /**
     * Gets the physics data for the state.
     *
     * @return The physics data
     */
    @Override
    public IPhysicsData getPhysics() {
        return physics;
    }

    /**
     * Gets the graphics data for the state.
     *
     * @return The graphics data
     */
    @Override
    public IGraphicsData getGraphics() {
        return graphics;
    }

    /**
     * Gets the name of the state.
     *
     * @return The state name (EState)
     */
    @Override
    public EState getName() {
        return name;
    }
}
