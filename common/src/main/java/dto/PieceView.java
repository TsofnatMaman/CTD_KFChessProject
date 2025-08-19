package dto;

import board.BoardConfig;
import interfaces.IBoard;
import interfaces.IPiece;
import interfaces.IPlayer;
import pieces.EPieceType;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record PieceView(BufferedImage frame,
        double x,
        double y){

    public static PieceView from(IPiece piece, BoardConfig bc) {
        var physics = piece.getCurrentState().getPhysics();
        return new PieceView(
                piece.getCurrentState().getGraphics().getCurrentFrame(),
                (physics.getCurrentX() / bc.physicsDimension().getWidth())* bc.panelDimension().getWidth(), (physics.getCurrentY() / bc.physicsDimension().getHeight())* bc.panelDimension().getHeight()
        );
    }

    public static List<PieceView> toPieceViews(IBoard board) {
        return Arrays.stream(board.getPlayers())
                .flatMap((IPlayer player) -> player.getPieces().stream())
                .filter(piece -> !piece.isCaptured())
                .map(p->PieceView.from(p,board.getBoardConfig()))
                .collect(Collectors.toList());
    }

}
