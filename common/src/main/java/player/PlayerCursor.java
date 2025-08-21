package player;

import interfaces.IPlayerCursor;
import pieces.Position;

import java.awt.*;

/**
 * Represents a player's cursor for selecting pieces on the board.
 * <p>
 * Tracks the cursor position, allows movement within board boundaries,
 * and provides a method to draw the cursor on a panel.
 * </p>
 */
public class PlayerCursor implements IPlayerCursor {

    private final Position pos;
    private final Color color;
    public final int ROWS;
    public final int COLS;

    /**
     * Constructs a PlayerCursor with an initial position and color.
     *
     * @param pos   The initial position of the cursor
     * @param color The color used to draw the cursor
     */
    public PlayerCursor(Position pos, Color color) {
        ROWS = constants.GameConstants.BOARD_ROWS;
        COLS = constants.GameConstants.BOARD_COLS;
        this.pos = pos;
        this.color = color;
    }

    // ===== Cursor Movement =====

    @Override
    public void moveUp() {
        if (pos.getRow() > 0) pos.reduceOneRow();
    }

    @Override
    public void moveDown() {
        if (pos.getRow() < ROWS - 1) pos.addOneRow();
    }

    @Override
    public void moveLeft() {
        if (pos.getCol() > 0) pos.reduceOneCol();
    }

    @Override
    public void moveRight() {
        if (pos.getCol() < COLS - 1) pos.addOneCol();
    }

    // ===== Drawing =====

    /**
     * Draws the cursor as a rectangle on the panel.
     *
     * @param g           the Graphics object used for drawing
     * @param panelWidth  width of the panel
     * @param panelHeight height of the panel
     */
    @Override
    public void draw(Graphics g, int panelWidth, int panelHeight) {
        int squareWidth = panelWidth / COLS;
        int squareHeight = panelHeight / ROWS;

        int x = pos.getCol() * squareWidth;
        int y = pos.getRow() * squareHeight;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, squareWidth, squareHeight);
    }

    // ===== Getters =====

    @Override
    public Position getPosition() {
        return pos;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Cursor at row=" + pos.getRow() + ", col=" + pos.getCol();
    }
}
