package viewUtils;

import constants.GameConstants;
import interfaces.IPiece;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

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
    public static void draw(Graphics g, IPiece p, int squareWidth, int squareHeight) {
        BufferedImage frame = p.getCurrentState().getGraphics().getCurrentFrame();

        Point2D.Double pos = p.getCurrentPixelPosition();
        int pixelX = (int) (pos.x * squareWidth / GameConstants.SQUARE_SIZE);
        int pixelY = (int) (pos.y * squareHeight / GameConstants.SQUARE_SIZE);

        g.drawImage(frame, pixelX, pixelY, squareWidth, squareHeight, null);
    }

}
