package pieces;

/**
 * Enum representing different chess piece types with their properties.
 * Each piece type has a letter code, movement capability, and point value.
 */
public enum EPieceType {
    /** Bishop - can only move diagonally (code and score extracted to PieceConstants if needed) */
    B("B", false, 3),
    /** King - most important piece, can move one square in any direction (code and score extracted to PieceConstants if needed) */
    K("K", false, 0),
    /** Knight - can jump over other pieces (code and score extracted to PieceConstants if needed) */
    N("N", true, 3),
    /** Pawn - basic piece that can be promoted (code and score extracted to PieceConstants if needed) */
    P("P", false, 1),
    /** Queen - most powerful piece, can move in any direction (code and score extracted to PieceConstants if needed) */
    Q("Q", false, 9),
    /** Rook - can move horizontally and vertically (code and score extracted to PieceConstants if needed) */
    R("R", false, 5);

    /** The letter code representing this piece type */
    private final String val;
    /** Whether this piece can jump over other pieces */
    private final boolean canSkip;
    /** The point value of this piece type */
    private final int score;

    /**
     * Constructs a piece type with its properties.
     * 
     * @param val The letter code for this piece type
     * @param canSkip Whether this piece can jump over others
     * @param score The point value of this piece
     */
    EPieceType(String val, boolean canSkip, int score) {
        this.val = val;
        this.canSkip = canSkip;
        this.score = score;
    }

    /**
     * Gets the letter code for this piece type.
     * @return The piece's letter code
     */
    public String getVal() {
        return val;
    }

    /**
     * Checks if this piece type can jump over other pieces.
     * @return true if the piece can jump over others
     */
    public boolean isCanSkip() {
        return canSkip;
    }

    /**
     * Gets the point value of this piece type.
     * @return The piece's score value
     */
    public int getScore() {
        return score;
    }
}
