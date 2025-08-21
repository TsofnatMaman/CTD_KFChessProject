package dto;

import board.BoardConfig;
import interfaces.IBoard;
import interfaces.IPiece;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a visual snapshot of a piece on the board.
 * Contains the current frame of the piece and its screen coordinates.
 *
 * @param frame Current graphical frame of the piece
 * @param x X-coordinate on the panel
 * @param y Y-coordinate on the panel
 */
public record PieceView(BufferedImage frame, double x, double y) {

    /**
     * Creates a PieceView from a piece and board configuration.
     *
     * @param piece The piece to visualize
     * @param bc Board configuration for scaling
     * @return PieceView representing the piece's visual state
     */
    public static PieceView from(IPiece piece, BoardConfig bc) {
        var physics = piece.getCurrentState().getPhysics();
        return new PieceView(
                piece.getCurrentState().getGraphics().getCurrentFrame(),
                (physics.getCurrentX() / bc.physicsDimension().getWidth()) * bc.panelDimension().getWidth(),
                (physics.getCurrentY() / bc.physicsDimension().getHeight()) * bc.panelDimension().getHeight()
        );
    }

    /**
     * Converts all non-captured pieces on the board into PieceViews.
     *
     * @param board Board containing players and pieces
     * @return List of PieceView objects for rendering
     */
    public static List<PieceView> toPieceViews(IBoard board) {
        return Arrays.stream(board.getPlayers())
                .flatMap(player -> player.getPieces().stream())
                .filter(piece -> !piece.isCaptured())
                .map(p -> PieceView.from(p, board.getBoardConfig()))
                .toList();
    }
}
