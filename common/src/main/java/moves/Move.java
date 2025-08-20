package moves;

import java.util.Objects;

/**
 * Represents a single move with delta x and delta y,
 * and optional conditions that must be satisfied for the move to be valid.
 *
 * @param dx        Delta row
 * @param dy        Delta column
 * @param condition Conditions that must be satisfied for this move
 */
public record Move(int dx, int dy, ECondition[] condition) {
    /**
     * Constructs a move with the given delta x and delta y and conditions.
     *
     * @param dx        Delta row (change in row)
     * @param dy        Delta column (change in column)
     * @param condition Array of conditions for this move
     */
    public Move {
    }
}
