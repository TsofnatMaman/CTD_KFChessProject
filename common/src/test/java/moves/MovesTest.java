package moves;

import org.junit.jupiter.api.Test;
import pieces.EPieceType;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Moves utility class, which loads Move objects from resources.
 */
class MovesTest {

    @Test
    void testCreateMovesList_validResource() throws IOException {
        // Assume a resource file exists for the Rook (R) moves for player 0
        List<Move> moves = Moves.createMovesList(EPieceType.R, 0);

        assertNotNull(moves, "The list of moves should not be null");
        assertFalse(moves.isEmpty(), "The list should contain at least one Move");

        // Basic check of the first Move object
        Move firstMove = moves.get(0);
        assertNotNull(firstMove, "First Move should not be null");
        assertTrue(firstMove.dx() != 0 || firstMove.dy() != 0, "Move should have non-zero delta");

        if (firstMove.condition() != null) {
            for (ECondition cond : firstMove.condition()) {
                assertNotNull(cond, "Each condition should not be null");
            }
        }
    }

    @Test
    void testCreateMovesList_nonExistentResource() {
        // Attempt to create moves for a piece type and player with no resource file
        EPieceType fakeType = EPieceType.P; // Assume player 99 has no resource
        IOException thrown = assertThrows(IOException.class, () -> Moves.createMovesList(fakeType, 99));
        assertTrue(thrown.getMessage().contains("Resource not found"), "Exception message should mention missing resource");
    }

    @Test
    void testCreateMovesList_conditionsParsing() throws IOException {
        // Verify that conditions are correctly parsed to ECondition
        List<Move> moves = Moves.createMovesList(EPieceType.R, 0);
        for (Move m : moves) {
            if (m.condition() != null) {
                for (ECondition c : m.condition()) {
                    assertTrue(c instanceof ECondition, "Parsed condition should be an instance of ECondition");
                }
            }
        }
    }
}
