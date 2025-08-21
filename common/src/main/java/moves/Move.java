package moves;

/**
 * Represents a single move in the game with row and column deltas,
 * along with optional conditions that must be satisfied for the move to be valid.
 * <p>
 * Typically used to define piece movement patterns.
 * </p>
 *
 * @param dx        Change in row (delta x)
 * @param dy        Change in column (delta y)
 * @param condition Array of conditions that must be satisfied for this move
 */
public record Move(int dx, int dy, ECondition[] condition) {

    /**
     * Canonical constructor for the Move record.
     *
     * @param dx        Delta row (change in row)
     * @param dy        Delta column (change in column)
     * @param condition Array of conditions that must be satisfied for this move
     */
    public Move {
        // Record canonical constructor; logic is handled by fields.
    }
}
