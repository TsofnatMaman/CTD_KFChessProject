package viewUtils;

import board.BoardConfig;
import dto.PieceView;

import java.awt.*;
import java.util.List;

/**
 * Utility class for rendering the board and its pieces.
 */
public class BoardRenderer {
    public static void draw(Graphics g, List<PieceView> pieces, BoardConfig bc) {
        int squareWidth = (int) (bc.panelDimension().getWidth() / bc.gridDimension().getWidth());
        int squareHeight = (int) (bc.panelDimension().getHeight() / bc.gridDimension().getHeight());

        for(PieceView p:pieces){
            PieceRenderer.draw(g, p, squareWidth, squareHeight);
        }
    }
}
