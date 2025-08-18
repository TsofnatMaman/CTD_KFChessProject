package interfaces;

import pieces.EPieceEvent;
import pieces.Position;
import state.EState;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Optional;

/**
 * Interface for piece state operations.
 */
public interface IState extends Serializable {

    /**
     * Resets the state to a new action.
     * @param from The starting position
     * @param to The target position
     */
//    void reset(EState state, Position from, Position to);

    void reset(Position from, Position to);

    /**
     * Updates the physics and graphics for the current state.
     */
    Optional<EPieceEvent> update();

    /**
     * Checks if the current action is finished.
     * @return true if finished, false otherwise
     */
    boolean isActionFinished();

    Point2D.Double getCurrentPosition();

    /**
     * Gets the physics data for the state.
     * @return The physics data
     */
    IPhysicsData getPhysics();

    /**
     * Gets the graphics data for the state.
     * @return The graphics data
     */
    IGraphicsData getGraphics();

    EState getName();
}
