package state;

import board.BoardConfig;
import interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pieces.EPieceEvent;
import pieces.Position;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the State class.
 * Verifies reset behavior, update behavior, and action completion delegation.
 */
class StateTest {

    @Mock
    private IPhysicsData physics;   // Mocked physics component

    @Mock
    private IGraphicsData graphics; // Mocked graphics component

    private BoardConfig bc;          // Board configuration for the state
    private Position startPos;       // Starting position for the state
    private Position targetPos;      // Target position for the state
    private State state;             // State instance under test

    @BeforeEach
    void setup() {
        // Initialize Mockito annotations for mocks
        MockitoAnnotations.openMocks(this);

        // Initialize BoardConfig (can adjust parameters as needed)
        bc = new BoardConfig(null, null, null);

        // Initialize start and target positions
        startPos = new Position(0, 0);
        targetPos = new Position(1, 0);

        // Create a State instance using the mocked physics and graphics
        state = new State(EState.IDLE, startPos, targetPos, bc, physics, graphics);
    }

    /**
     * Test that reset() properly updates the physics and graphics components.
     * Ensures that graphics.reset() and physics.reset(...) are called with correct parameters.
     */
    @Test
    void testResetUpdatesPhysicsAndGraphics() {
        state.reset(startPos, targetPos);

        // Verify that graphics.reset() was called once
        verify(graphics, times(1)).reset();

        // Verify that physics.reset() was called once with correct parameters
        verify(physics, times(1)).reset(
                eq(EState.IDLE),       // State enum
                eq(startPos),          // Start position
                eq(targetPos),         // Target position
                eq(bc),                // Board configuration
                anyLong()              // startTime is checked as any long value
        );
    }

    /**
     * Test that update() calls both physics and graphics update methods.
     * Also checks that update returns Optional.empty() when action is not finished.
     */
    @Test
    void testUpdateCallsPhysicsAndGraphics() {
        // Simulate that the action has not yet finished
        when(physics.isActionFinished(anyLong())).thenReturn(false);

        long now = System.nanoTime();
        Optional<EPieceEvent> result = state.update(now);

        // Verify that graphics.update() and physics.update() were called with the current time
        verify(graphics, times(1)).update(now);
        verify(physics, times(1)).update(now);

        // Assert that update() returns empty since action is not finished
        assertTrue(result.isEmpty());
    }

    /**
     * Test that isActionFinished() delegates directly to the physics component.
     */
    @Test
    void testIsActionFinishedDelegatesToPhysics() {
        long now = System.nanoTime();
        // Simulate that physics reports the action is finished
        when(physics.isActionFinished(now)).thenReturn(true);

        // Assert that state.isActionFinished(now) returns true
        assertTrue(state.isActionFinished(now));

        // Verify that the physics.isActionFinished() method was called exactly once
        verify(physics, times(1)).isActionFinished(now);
    }
}
