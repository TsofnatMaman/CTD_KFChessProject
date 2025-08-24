package state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.EPieceEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
     * Test that next() throws when a transition is not defined in the CSV.
     * Two cases are checked: missing event and missing state mapping.
     */
    @Test
    void testNextThrowsForInvalidTransition() {
        assertThrows(IllegalStateException.class, () -> table.next(EState.IDLE, EPieceEvent.DONE));
        assertThrows(IllegalStateException.class, () -> table.next(EState.JUMP, EPieceEvent.MOVE));
    }
}
