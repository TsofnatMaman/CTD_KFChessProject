package moves;

import java.util.Objects;

/**
 * Represents a single move with delta x and delta y,
 * and optional conditions that must be satisfied for the move to be valid.
 */
public class Move {
    /** Delta row */
    private final int dx;
    /** Delta column */
    private final int dy;
    /** Conditions that must be satisfied for this move */
    private final ECondition[] condition;

    /**
     * Constructs a move with the given delta x and delta y and conditions.
     *
     * @param dx        Delta row (change in row)
     * @param dy        Delta column (change in column)
     * @param condition Array of conditions for this move
     */
    public Move(int dx, int dy, ECondition[] condition) {
        this.dx = dx;
        this.dy = dy;
        this.condition = condition;
    }

    /** @return Delta row */
    public int getDx() {
        return dx;
    }

    /** @return Delta column */
    public int getDy() {
        return dy;
    }

    /** @return Array of move conditions */
    public ECondition[] getCondition() {
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return dx == move.dx && dy == move.dy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dx, dy);
    }
}
