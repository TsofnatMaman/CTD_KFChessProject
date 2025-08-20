package interfaces;

import pieces.EPieceEvent;
import pieces.Position;
import state.EState;

import java.io.Serializable;
import java.util.Optional;

/**
 * Interface for managing a piece's state, including movement, animation, and actions.
 */
public interface IState extends Serializable {

    /**
     * Resets the state for a new action with the given start and target positions.
     *
     * @param from Starting position
     * @param to   Target position
     */
    void reset(Position from, Position to);

    /**
     * Updates the state, including physics and graphics, and optionally returns an event.
     *
     * @param now Current time in nanoseconds
     * @return Optional EPieceEvent triggered by the update
     */
    Optional<EPieceEvent> update(long now);

    /**
     * Checks if the current action has finished.
     *
     * @param now Current time in nanoseconds
     * @return true if the action is finished, false otherwise
     */
    boolean isActionFinished(long now);

    /**
     * Gets the physics data associated with this state.
     *
     * @return IPhysicsData instance
     */
    IPhysicsData getPhysics();

    /**
     * Gets the graphics data associated with this state.
     *
     * @return IGraphicsData instance
     */
    IGraphicsData getGraphics();

    /**
     * Gets the name/type of the current state.
     *
     * @return EState representing the state's name
     */
    EState getName();
}
