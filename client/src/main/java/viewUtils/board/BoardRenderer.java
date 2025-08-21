package viewUtils.board;

import board.BoardConfig;
import dto.PieceView;

import java.awt.*;
import java.util.List;

/**
 * Utility class responsible for rendering the game board and its pieces.
 * <p>
 * This class does not store state — it only provides static rendering
 * methods that receive the necessary data (pieces and configuration).
 * </p>
 */
public class BoardRenderer {

    /**
     * Draws all pieces on the game board.
     *
     * @param g      the {@link Graphics} context used for rendering
     * @param pieces the list of pieces to be drawn on the board
     * @param bc     the board configuration (dimensions, grid size, etc.)
     */
    public static void draw(Graphics g, List<PieceView> pieces, BoardConfig bc) {
        // Compute the width and height of a single grid square on the board
        int squareWidth = (int) (bc.panelDimension().getWidth() / bc.gridDimension().getWidth());
        int squareHeight = (int) (bc.panelDimension().getHeight() / bc.gridDimension().getHeight());

        // Draw each piece using the PieceRenderer utility class
        for (PieceView p : pieces) {
            PieceRenderer.draw(g, p, squareWidth, squareHeight);
        }
    }
}
