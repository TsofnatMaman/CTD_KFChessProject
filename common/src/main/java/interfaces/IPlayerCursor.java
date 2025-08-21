package interfaces;

import pieces.Position;

import java.awt.*;

/**
 * Interface representing a player's cursor on the board.
 */
public interface IPlayerCursor {

    /** Moves the cursor one row up. */
    void moveUp();

    /** Moves the cursor one row down. */
    void moveDown();

    /** Moves the cursor one column left. */
    void moveLeft();

    /** Moves the cursor one column right. */
    void moveRight();

    /**
     * Draws the cursor on the game board.
     *
     * @param g           The graphics context
     * @param panelWidth  Width of the board panel in pixels
     * @param panelHeight Height of the board panel in pixels
     */
    void draw(Graphics g, int panelWidth, int panelHeight);

    /** Returns the current position of the cursor. */
    Position getPosition();

    /** Returns the color of the cursor for rendering purposes. */
    Color getColor();
}
