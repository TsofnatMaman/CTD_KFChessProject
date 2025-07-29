package pieces;

/**
 * Enum representing different chess piece types with their properties.
 * Each piece type has a letter code, movement capability, and point value.
 */
public enum EPieceType {
    /** Bishop - can only move diagonally */
    B("B", false, 1),
    /** King - most important piece, can move one square in any direction */
    K("K", false, 100),
    /** Knight - can jump over other pieces */
    N("N", true, 50),
    /** Pawn - basic piece that can be promoted */
    P("P", false, 20),
    /** Queen - most powerful piece, can move in any direction */
    Q("Q", false, 80),
    /** Rook - can move horizontally and vertically */
    R("R", false, 30);

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
