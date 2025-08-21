package moves;

import interfaces.IBoard;
import interfaces.IPiece;
import pieces.Position;

/**
 * Immutable container class used for move validation.
 * <p>
 * This object groups together:
 * <ul>
 *   <li>The current game board state</li>
 *   <li>The piece attempting to move</li>
 *   <li>The target position of the move</li>
 * </ul>
 * It is typically passed to move/jump condition checks to simplify validation logic.
 * </p>
 */
public class Data {

    /** Reference to the current game board. */
    public final IBoard board;

    /** The piece that is attempting to move. */
    public final IPiece pieceFrom;

    /** The target position for the move. */
    public final Position to;

    /**
     * Constructs a new {@code Data} object for move validation.
     *
     * @param board     the current game board
     * @param fromPiece the piece attempting to move
     * @param to        the target position for the move
     */
    public Data(IBoard board, IPiece fromPiece, Position to) {
        this.board = board;
        this.pieceFrom = fromPiece;
        this.to = to;
    }
}
