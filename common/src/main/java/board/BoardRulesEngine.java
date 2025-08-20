package board;

import interfaces.IBoard;
import pieces.Position;

public class BoardRulesEngine {
    public static boolean isMoveLegal(IBoard board, Position from, Position to) {
        return board.isInBounds(from) && board.isInBounds(to) && board.isMoveLegal(from, to);
    }

    public static boolean isJumpLegal(IBoard board, Position pos) {
        return board.isInBounds(pos) && board.isJumpLegal(board.getPiece(pos));
    }
}
