package player;

import interfaces.IPlayerCursor;
import pieces.Position;

import java.awt.*;

/**
 * Represents a player's cursor for selecting pieces on the board.
 */
public class PlayerCursor implements IPlayerCursor {
    private Position pos;
    private final Color color;
    public final int ROWS;
    public final int COLS;

    /**
     * Constructs a PlayerCursor for selecting pieces.
     * @param pos The initial position of the cursor
     * @param color The color used to draw the cursor
     */
    public PlayerCursor(Position pos, Color color) {
        ROWS=8;
        COLS=8;
        this.pos = pos;
        this.color = color;
    }

    /**
     * Moves the cursor up by one row if possible.
     */
    @Override
    public void moveUp() {
        if (pos.getRow() > 0) pos.reduceOneRow();
    }

    /**
     * Moves the cursor down by one row if possible.
     */
    @Override
    public void moveDown() {
        if (pos.getRow() < ROWS-1) pos.addOneRow();
    }

    /**
     * Moves the cursor left by one column if possible.
     */
    @Override
    public void moveLeft() {
        if (pos.getCol() > 0) pos.reduceOneCol();
    }

    /**
     * Moves the cursor right by one column if possible.
     */
    @Override
    public void moveRight() {
        if (pos.getCol() < COLS-1) pos.addOneCol();
    }

    /**
     * Draws the cursor on the board panel.
     * @param g Graphics context
     * @param panelWidth Width of the panel
     * @param panelHeight Height of the panel
     */
    @Override
    public void draw(Graphics g, int panelWidth, int panelHeight) {
        int squareWidth = panelWidth / ROWS;
        int squareHeight = panelHeight / COLS;

        int x = pos.getCol() * squareWidth;
        int y = pos.getRow() * squareHeight;

        Graphics2D g2d = (Graphics2D) g;  // Convert to Graphics2D

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3));  // Use thicker stroke for cursor
        g2d.drawRect(x, y, squareWidth, squareHeight);
    }

    /**
     * Gets the current row of the cursor.
     * @return The row index
     */
    @Override
    public int getRow() {
        return pos.getRow();
    }

    /**
     * Gets the current column of the cursor.
     * @return The column index
     */
    @Override
    public int getCol() {
        return pos.getCol();
    }

    /**
     * Gets the current position of the cursor.
     * @return The position object
     */
    @Override
    public Position getPosition(){
        return pos;
    }
}
