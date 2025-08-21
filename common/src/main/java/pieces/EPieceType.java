package pieces;

/**
 * Enum representing different chess piece types with associated properties.
 * Each piece type has a letter code, jump capability, and point value.
 */
public enum EPieceType {
    /** Bishop - moves diagonally */
    B("B", false, 3),

    /** King - moves one square in any direction */
    K("K", false, 0),

    /** Knight - can jump over other pieces */
    N("N", true, 3),

    /** Pawn - basic piece, can be promoted */
    P("P", false, 1),

    /** Queen - moves in any direction */
    Q("Q", false, 9),

    /** Rook - moves horizontally and vertically */
    R("R", false, 5);

    private final String val;       // Letter code representing this piece type
    private final boolean canSkip;  // Whether this piece can jump over others
    private final int score;        // Point value of this piece

    EPieceType(String val, boolean canSkip, int score) {
        this.val = val;
        this.canSkip = canSkip;
        this.score = score;
    }

    /** @return Letter code of this piece type */
    public String getVal() {
        return val;
    }

    /** @return True if this piece can jump over other pieces */
    public boolean isCanSkip() {
        return canSkip;
    }

    /** @return Point value of this piece type */
    public int getScore() {
        return score;
    }
}
