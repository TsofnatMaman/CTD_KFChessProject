package viewUtils.board;

import dto.PieceView;

import java.awt.*;

/**
 * Utility class responsible for rendering individual pieces on the board.
 */
public class PieceRenderer {

    /**
     * Draws a single piece on the board using its current animation frame.
     *
     * @param g            Graphics context
     * @param p            PieceView containing piece position and current frame
     * @param squareWidth  Width of a single board square
     * @param squareHeight Height of a single board square
     */
    public static void draw(Graphics g, PieceView p, int squareWidth, int squareHeight) {
        // Draw the piece's current frame scaled to the square dimensions
        g.drawImage(
                p.frame(),               // current animation frame
                (int) p.x(),             // x-position in pixels
                (int) p.y(),             // y-position in pixels
                squareWidth,             // width of the piece
                squareHeight,            // height of the piece
                null                     // no ImageObserver needed
        );
    }
}
