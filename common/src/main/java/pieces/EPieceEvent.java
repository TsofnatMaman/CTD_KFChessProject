package pieces;

/**
 * Enum representing the different events a piece can perform.
 */
public enum EPieceEvent {
    /** Normal move of a piece */
    MOVE,

    /** Jump action (typically for capture) */
    JUMP,

    /** Piece has finished its current action */
    DONE
}
