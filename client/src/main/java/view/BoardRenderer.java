package view;

import interfaces.IBoard;
import interfaces.IPiece;
import interfaces.IPlayer;

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

        for(IPlayer p:board.getPlayers()){
            for(IPiece piece:p.getPieces()){
                if(!piece.isCaptured())
                    PieceRenderer.draw(g, piece, squareWidth, squareHeight);

            }
        }
    }
}
