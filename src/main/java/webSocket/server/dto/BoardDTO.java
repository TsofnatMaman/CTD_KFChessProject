package webSocket.server.dto;

import interfaces.IBoard;
import interfaces.IPiece;

import java.util.ArrayList;
import java.util.List;

/**
 * תיאור פשוט של הלוח וכל הכלים בו.
 */
public class BoardDTO {
    public int rows;
    public int cols;
    public PieceDTO[][] boardGrid;

    public static BoardDTO from(IBoard board) {
        BoardDTO snap = new BoardDTO();
        snap.rows = board.getROWS();
        snap.cols = board.getCOLS();

        snap.boardGrid = new PieceDTO[snap.rows][snap.cols];

        for (int r = 0; r < snap.rows; r++) {
            for (int c = 0; c < snap.cols; c++) {
                IPiece p = board.getPiece(r, c);
                if (p != null && !p.isCaptured()) {
                    snap.boardGrid[r][c] = PieceDTO.from(p);
                }
            }
        }

        return snap;
    }
}
