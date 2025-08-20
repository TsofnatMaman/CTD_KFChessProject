package moves;

import interfaces.IBoard;
import interfaces.IPiece;
import pieces.Position;

/**
 * Container class for move validation data.
 * Holds references to the game board, the piece being moved, and the target position.
 * Useful for move and jump condition checks.
 */
public class Data {

    /** Reference to the current game board */
    public final IBoard board;

    /** The piece attempting to move */
    public final IPiece pieceFrom;

    /** The target position for the move */
    public final Position to;

    /**
     * Constructs a new Data object for move validation.
     *
     * @param board     The current game board
     * @param fromPiece The piece that is attempting to move
     * @param to        The target position for the move
     */
    public Data(IBoard board, IPiece fromPiece, Position to) {
        this.board = board;
        this.pieceFrom = fromPiece;
        this.to = to;
    }
}
