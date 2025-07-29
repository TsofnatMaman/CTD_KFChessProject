package interfaces;

import pieces.Position;

import java.awt.*;

/**
 * Interface for player cursor operations.
 */
public interface IPlayerCursor {
    /**
     * Moves the cursor up by one row.
     */
    void moveUp();

    /**
     * Moves the cursor down by one row.
     */
    void moveDown();

    /**
     * Moves the cursor left by one column.
     */
    void moveLeft();

    /**
     * Moves the cursor right by one column.
     */
    void moveRight();

    /**
     * Draws the cursor on the board panel.
     *
     * @param g          Graphics context
     * @param panelWidth Width of the panel
     * @param panelHeight Height of the panel
     */
    void draw(Graphics g, int panelWidth, int panelHeight);

    /**
     * Gets the current row of the cursor.
     *
     * @return The row index
     */
    int getRow();

    /**
     * Gets the current column of the cursor.
     *
     * @return The column index
     */
    int getCol();

    /**
     * Gets the current position of the cursor.
     *
     * @return The position object
     */
    Position getPosition();
}
