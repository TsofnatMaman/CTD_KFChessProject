package board;

import interfaces.IBoard;
import interfaces.IPiece;
import pieces.PieceRenderer;

import java.awt.*;

/**
 * Utility class for rendering the board and its pieces.
 */
public class BoardRenderer {
    /**
     * Draws all pieces on the board.
     * @param g Graphics context
     * @param board The board to draw
     * @param panelWidth Width of the panel
     * @param panelHeight Height of the panel
     */
    public static void draw(Graphics g, IBoard board, int panelWidth, int panelHeight) {
        int squareWidth = panelWidth / board.getCOLS();
        int squareHeight = panelHeight / board.getROWS();

        for (int row = 0; row < board.getROWS(); row++) {
            for (int col = 0; col < board.getCOLS(); col++) {
                IPiece p = board.getPiece(row, col);
                if (p != null) {
                    PieceRenderer.draw(g, p, squareWidth, squareHeight);
                }
            }
        }
    }
}
