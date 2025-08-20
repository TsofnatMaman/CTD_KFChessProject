package dto;

import board.BoardConfig;
import interfaces.IBoard;
import interfaces.IPiece;
import interfaces.IPlayer;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a visual snapshot of a piece on the board.
 * Contains the current frame of the piece and its screen coordinates.
 *
 * @param frame the current graphical frame of the piece
 * @param x x-coordinate of the piece on the panel
 * @param y y-coordinate of the piece on the panel
 */
public record PieceView(BufferedImage frame, double x, double y) {

    /**
     * Creates a PieceView from an IPiece and board configuration.
     *
     * @param piece the piece to visualize
     * @param bc board configuration for scaling
     * @return a PieceView representing the piece's current visual state
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
     * Converts all non-captured pieces on the board into a list of PieceViews.
     *
     * @param board the board containing players and pieces
     * @return list of PieceView objects for rendering
     */
    public static List<PieceView> toPieceViews(IBoard board) {
        return Arrays.stream(board.getPlayers())
                .flatMap((IPlayer player) -> player.getPieces().stream())
                .filter(piece -> !piece.isCaptured())
                .map(p -> PieceView.from(p, board.getBoardConfig()))
                .toList();
    }
}
