package viewUtils.board;

import board.BoardConfig;
import dto.PieceView;

import java.awt.*;
import java.util.List;

/**
 * Utility class responsible for rendering the game board and all its pieces.
 */
public class BoardRenderer {

    /**
     * Draws all pieces on the board.
     *
     * @param g      Graphics context
     * @param pieces List of pieces to draw
     * @param bc     Board configuration (size, grid)
     */
    public static void draw(Graphics g, List<PieceView> pieces, BoardConfig bc) {
        // Compute width and height of a single square on the board
        int squareWidth = (int) (bc.panelDimension().getWidth() / bc.gridDimension().getWidth());
        int squareHeight = (int) (bc.panelDimension().getHeight() / bc.gridDimension().getHeight());

        // Draw each piece using the PieceRenderer utility
        for (PieceView p : pieces) {
            PieceRenderer.draw(g, p, squareWidth, squareHeight);
        }
    }
}
