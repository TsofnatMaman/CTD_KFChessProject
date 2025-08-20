package interfaces;

import pieces.Position;

import java.awt.*;

/**
 * Interface representing a player's cursor on the board.
 */
public interface IPlayerCursor {

    /**
     * Moves the cursor one row up.
     */
    void moveUp();

    /**
     * Moves the cursor one row down.
     */
    void moveDown();

    /**
     * Moves the cursor one column left.
     */
    void moveLeft();

    /**
     * Moves the cursor one column right.
     */
    void moveRight();

    /**
     * Draws the cursor on the game board.
     *
     * @param g          The graphics context
     * @param panelWidth The width of the board panel
     * @param panelHeight The height of the board panel
     */
    void draw(Graphics g, int panelWidth, int panelHeight);

    /**
     * Gets the current position of the cursor.
     *
     * @return Current cursor position
     */
    Position getPosition();

    /**
     * Gets the color of the cursor for rendering.
     *
     * @return Cursor color
     */
    Color getColor();
}
