package moves;

import java.util.Objects;

/**
 * Represents a single move with delta x and delta y.
 */
public class Move {
    int dx;
    int dy;
    ECondition condition;

    /**
     * Constructs a move with the given delta x and delta y.
     * @param dx Delta x (row difference)
     * @param dy Delta y (column difference)
     */
    Move(int dx, int dy, ECondition condition) {
        this.dx = dx;
        this.dy = dy;
        this.condition = condition;
    }

    /**
     * Gets the delta x of the move.
     * @return Delta x value
     */
    public int getDx() {
        return dx;
    }

    /**
     * Gets the delta y of the move.
     * @return Delta y value
     */
    public int getDy() {
        return dy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return getDx() == move.getDx() && getDy() == move.getDy();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDx(), getDy());
    }

    public ECondition getCondition() {
        return condition;
    }
}
