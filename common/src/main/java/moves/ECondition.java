package moves;

import java.util.function.Predicate;

/**
 * Enum representing different conditions for validating piece moves.
 * Each condition is associated with a predicate that tests whether
 * a move is valid under specific circumstances.
 */
public enum ECondition {
    /** Validates that the target square is empty */
    NON_CAPTURE(d -> d.board.getPiece(d.to) == null),
    
    /** Validates that the piece hasn't moved from its starting position */
    FIRST_TIME(d -> d.pieceFrom.getId().equals(d.pieceFrom.getPos().toString())),
    
    /** Validates that the target square contains a piece that can be captured */
    CAPTURE(d -> d.board.getPiece(d.to) != null);

    
    /** The predicate used to test the condition */
    private final Predicate<Data> condition;

    /**
     * Constructs a condition with the specified validation predicate.
     * 
     * @param condition The predicate that defines the move validation logic
     */
    ECondition(Predicate<Data> condition) {
        this.condition = condition;
    }

    /**
     * Tests if a move is valid under this condition.
     * 
     * @param data The move data to validate
     * @return true if the move is valid under this condition, false otherwise
     */
    public boolean isCanMove(Data data) {
        return condition.test(data);
    }
}
