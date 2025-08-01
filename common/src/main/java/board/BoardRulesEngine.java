package board;

import interfaces.IBoard;
import interfaces.IPiece;
import pieces.Position;

public class BoardRulesEngine {
    public static boolean isMoveLegal(IBoard board, Position from, Position to) {
        return board.isInBounds(from) && board.isInBounds(to) && board.isMoveLegal(from, to);
    }

    public static boolean isJumpLegal(IBoard board, IPiece piece) {
        return board.isJumpLegal(piece);
    }
}
