package viewUtils;

import dto.PieceView;

import java.awt.*;

/**
 * Utility class for rendering pieces on the board.
 */
public class PieceRenderer {
    /**
     * Draws a piece on the board using its current state and animation frame.
     * @param g Graphics context
     * @param p The piece to draw
     * @param squareWidth Width of a board square
     * @param squareHeight Height of a board square
     */
    public static void draw(Graphics g, PieceView p, int squareWidth, int squareHeight) {
        g.drawImage(p.frame(), (int) p.x(), (int) p.y(), squareWidth, squareHeight, null);
    }

}
