package state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.EPieceEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TransitionTable class.
 * Verifies correct state transitions, and proper handling of illegal transitions.
 */
class TransitionTableTest {

    private TransitionTable table; // Transition table instance under test

    @BeforeEach
    void setUp() {
        // Load the CSV file containing the transition table.
        // The path is inside resources and should start with '/'
        table = new TransitionTable("/state/transitionTable_test.csv");
    }

    /**
     * Test that valid transitions from the CSV are correctly loaded.
     * Checks multiple example transitions.
     */
    @Test
    void testLoadValidCsv() {
        // Verify that transitions defined in the CSV return the expected next state
        assertEquals(EState.MOVE, table.next(EState.IDLE, EPieceEvent.MOVE));
        assertEquals(EState.IDLE, table.next(EState.MOVE, EPieceEvent.DONE));
        assertEquals(EState.JUMP, table.next(EState.MOVE, EPieceEvent.JUMP));
        assertEquals(EState.IDLE, table.next(EState.JUMP, EPieceEvent.DONE));
    }

    /**
     * Test that an illegal transition throws an IllegalStateException.
     * Specifically, when a transition from IDLE with DONE event does not exist.
     */
    @Test
    void testIllegalTransitionThrows() {
        Exception ex = assertThrows(IllegalStateException.class,
                () -> table.next(EState.IDLE, EPieceEvent.DONE));
        // Verify that the exception message contains "Illegal transition"
        assertTrue(ex.getMessage().contains("Illegal transition"));
    }

    /**
     * Test that requesting a transition from a state-event pair
     * not present in the CSV throws an IllegalStateException.
     */
    @Test
    void testNullEventMapThrows() {
        Exception ex = assertThrows(IllegalStateException.class,
                () -> table.next(EState.JUMP, EPieceEvent.MOVE));
        // Verify that the exception message contains "Illegal transition"
        assertTrue(ex.getMessage().contains("Illegal transition"));
    }
}
