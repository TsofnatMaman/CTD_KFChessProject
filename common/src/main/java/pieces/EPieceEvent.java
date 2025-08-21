package pieces;

/**
 * Enum representing the different events a piece can trigger.
 */
public enum EPieceEvent {
    /** Standard move of the piece */
    MOVE,

    /** Jump action, typically for capturing */
    JUMP,

    /** Indicates the piece has completed its current action */
    DONE
}
