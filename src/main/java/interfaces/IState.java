package interfaces;

import pieces.Position;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Interface for piece state operations.
 */
public interface IState {

    /**
     * Resets the state to a new action.
     * @param state The new state
     * @param from The starting position
     * @param to The target position
     */
//    void reset(EState state, Position from, Position to);

    void reset(EState state, Position from, Position to);

    /**
     * Updates the physics and graphics for the current state.
     */
    void update();

    /**
     * Checks if the current action is finished.
     * @return true if finished, false otherwise
     */
    boolean isActionFinished();

    /**
     * Gets the starting column.
     * @return The starting column index
     */
    int getStartCol();

    /**
     * Gets the starting row.
     * @return The starting row index
     */
    int getStartRow();

    Point2D.Double getCurrentPosition();

    /**
     * Gets the target row.
     * @return The target row index
     */
    int getTargetRow();

    /**
     * Gets the target column.
     * @return The target column index
     */
    int getTargetCol();

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
}
