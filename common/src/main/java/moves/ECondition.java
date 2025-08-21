package moves;

import java.util.function.Predicate;

/**
 * Enum representing various move validation conditions for chess pieces.
 * <p>
 * Each enum value encapsulates a {@link Predicate} that determines whether
 * a move satisfies a specific rule, such as whether it is a capture or a first move.
 * </p>
 */
public enum ECondition {

    /** Validates that the target square is empty (non-capture move). */
    NON_CAPTURE(d -> d.board.getPiece(d.to) == null),

    /** Validates that the piece has not moved from its starting position (first move). */
    FIRST_TIME(d -> d.pieceFrom.isFirstMove()),

    /** Validates that the target square contains a piece that can be captured. */
    CAPTURE(d -> d.board.getPiece(d.to) != null);

    /** Predicate used to test whether the move satisfies this condition. */
    private final Predicate<Data> condition;

    /**
     * Constructs a move validation condition with the given predicate.
     *
     * @param condition the predicate defining the validation logic for this condition
     */
    ECondition(Predicate<Data> condition) {
        this.condition = condition;
    }

    /**
     * Tests whether a given move satisfies this condition.
     *
     * @param data the move data containing the board, piece, and target position
     * @return true if the move satisfies this condition, false otherwise
     */
    public boolean isCanMove(Data data) {
        return condition.test(data);
    }
}
