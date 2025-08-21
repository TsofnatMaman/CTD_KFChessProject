package viewUtils.board;

import dto.PieceView;

import java.awt.*;

/**
 * Utility class responsible for rendering individual pieces on the board.
 * <p>
 * Does not store any state; it simply draws a {@link PieceView} on a {@link Graphics} context.
 * </p>
 */
public class PieceRenderer {

    /**
     * Draws a single piece on the board using its current animation frame.
     * <p>
     * The piece is scaled to fit the size of a single board square.
     * </p>
     *
     * @param g            the {@link Graphics} context used for rendering
     * @param p            the {@link PieceView} containing piece position and current animation frame
     * @param squareWidth  the width of a single board square
     * @param squareHeight the height of a single board square
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
