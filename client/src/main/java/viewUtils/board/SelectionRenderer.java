package viewUtils.board;

import pieces.Position;

import java.awt.*;
import java.util.List;

/**
 * Utility class for rendering selection highlights and legal move indicators
 * on the board.
 */
public class SelectionRenderer {

    /**
     * Draws the selected square and the legal moves for a piece.
     * <p>
     * The selected square is filled with the given color, and legal moves
     * are drawn as smaller ovals inside the corresponding cells.
     * </p>
     *
     * @param g2       the {@link Graphics2D} context used for drawing
     * @param selected the currently selected position; if {@code null}, nothing is drawn
     * @param moves    list of legal move positions for the selected piece
     * @param color    the color used for highlighting the selection
     * @param cols     number of columns on the board
     * @param rows     number of rows on the board
     * @param width    total width of the board in pixels
     * @param height   total height of the board in pixels
     */
    public static void draw(Graphics2D g2, Position selected, List<Position> moves, Color color,
                            int cols, int rows, int width, int height) {

        if (selected == null) return;

        // Set highlight color
        g2.setColor(color);

        // Compute cell dimensions
        int cellW = width / cols;
        int cellH = height / rows;

        // Draw the selected square
        g2.fillRect(selected.getCol() * cellW, selected.getRow() * cellH, cellW, cellH);

        // Draw legal moves as smaller ovals inside each cell
        for (Position move : moves) {
            int x = move.getCol() * cellW + cellW / 4;
            int y = move.getRow() * cellH + cellH / 4;
            int w = cellW / 2;
            int h = cellH / 2;
            g2.fillOval(x, y, w, h);
        }
    }
}
