package moves;

import interfaces.IBoard;
import interfaces.IPiece;
import pieces.Position;

/**
 * Container class for move validation data.
 * Holds the board state, moving piece, and target position
 * for validating move conditions.
 */
public class Data {
    /** The game board reference */
    public final IBoard board;
    /** The piece being moved */
    public final IPiece pieceFrom;
    /** The target position for the move */
    public final Position to;

    /**
     * Constructs a new Data object for move validation.
     * 
     * @param board The current game board
     * @param fromPiece The piece that is attempting to move
     * @param to The target position for the move
     */
    public Data(IBoard board, IPiece fromPiece, Position to) {
        this.board = board;
        this.pieceFrom = fromPiece;
        this.to = to;
    }
}