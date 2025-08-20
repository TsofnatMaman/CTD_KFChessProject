package moves;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Move class, which represents a relative move with optional conditions.
 */
class MoveTest {

    @Test
    void testGetters() {
        // Arrange
        ECondition[] conditions = {ECondition.CAPTURE, ECondition.NON_CAPTURE};
        Move move = new Move(1, -2, conditions);

        // Assert
        assertEquals(1, move.dx());
        assertEquals(-2, move.dy());
        assertArrayEquals(conditions, move.condition());
    }

    @Test
    void testEquals_sameObject() {
        Move move = new Move(0, 1, null);
        assertEquals(move, move, "An object should equal itself");
    }

    @Test
    void testEquals_equalValues() {
        Move move1 = new Move(2, 3, null);
        Move move2 = new Move(2, 3, null);
        assertEquals(move1, move2, "Moves with the same dx, dy, and conditions should be equal");
    }

    @Test
    void testEquals_differentDx() {
        Move move1 = new Move(1, 3, null);
        Move move2 = new Move(2, 3, null);
        assertNotEquals(move1, move2, "Moves with different dx should not be equal");
    }

    @Test
    void testEquals_differentDy() {
        Move move1 = new Move(2, 1, null);
        Move move2 = new Move(2, 3, null);
        assertNotEquals(move1, move2, "Moves with different dy should not be equal");
    }

    @Test
    void testEquals_nullAndOtherClass() {
        Move move = new Move(1, 1, null);
        assertNotEquals(null, move, "Move should not be equal to null");
        assertNotEquals("not a move", move, "Move should not be equal to an object of different class");
    }

    @Test
    void testHashCode_consistency() {
        Move move1 = new Move(2, 3, null);
        Move move2 = new Move(2, 3, null);
        assertEquals(move1.hashCode(), move2.hashCode(), "Equal moves should have the same hash code");
    }

    @Test
    void testHashCode_difference() {
        Move move1 = new Move(2, 3, null);
        Move move2 = new Move(3, 2, null);
        assertNotEquals(move1.hashCode(), move2.hashCode(), "Different moves should ideally have different hash codes");
    }
}
