package interfaces;

import pieces.Position;

import java.util.List;

public interface IBoardEngine {
    boolean isMoveLegal(IBoard board, Position from, Position to);

    boolean isJumpLegal(IBoard board, Position pos);

    List<Position> getLegalMoves(IBoard board, Position pos);

    void handleUpdatePiece(IBoard board, IPlayer player, IPiece piece, long now);
}
