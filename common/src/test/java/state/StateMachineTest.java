package state;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import interfaces.IPhysicsData;
import interfaces.IState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pieces.EPieceEvent;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the StateMachine class.
 * Tests state transitions, update logic, and event publication.
 */
class StateMachineTest {

    private IState moveStateMock;       // Mock for the MOVE state
    private IState restStateMock;       // Mock for the LONG_REST state
    private IPhysicsData movePhysicsMock;  // Mock for physics data of MOVE state
    private IPhysicsData restPhysicsMock;  // Mock for physics data of LONG_REST state
    private StateMachine stateMachine;      // StateMachine instance under test

    @BeforeEach
    void setUp() {
        // Create mocks for states
        moveStateMock = mock(IState.class);
        restStateMock = mock(IState.class);

        // Create mocks for physics data associated with each state
        movePhysicsMock = mock(IPhysicsData.class);
        restPhysicsMock = mock(IPhysicsData.class);

        // Define behavior for state names
        when(moveStateMock.getName()).thenReturn(EState.MOVE);
        when(restStateMock.getName()).thenReturn(EState.LONG_REST);

        // Define behavior for physics retrieval from states
        when(moveStateMock.getPhysics()).thenReturn(movePhysicsMock);
        when(restStateMock.getPhysics()).thenReturn(restPhysicsMock);

        // Map of all states for the StateMachine
        Map<EState, IState> stateMap = Map.of(
                EState.MOVE, moveStateMock,
                EState.LONG_REST, restStateMock
        );

        // Mock the transition table
        TransitionTable transitionsMock = mock(TransitionTable.class);
        // Define expected transitions based on events
        when(transitionsMock.next(EState.MOVE, EPieceEvent.DONE)).thenReturn(EState.LONG_REST);
        when(transitionsMock.next(EState.LONG_REST, EPieceEvent.DONE)).thenReturn(EState.MOVE);

        // Initialize the StateMachine with MOVE as the starting state
        stateMachine = new StateMachine(stateMap, transitionsMock, EState.MOVE, mock(pieces.Position.class));
    }

    /**
     * Test that StateMachine properly transitions state when an event occurs.
     * Verifies that the new state's reset method is called with the correct positions.
     */
    @Test
    void testOnEventTransitionsState() {
        // Mock positions for event
        pieces.Position from = mock(pieces.Position.class);
        pieces.Position to = mock(pieces.Position.class);

        // Trigger the state transition event
        stateMachine.onEvent(EPieceEvent.DONE, from, to);

        // Verify that the new state's reset method was called
        verify(restStateMock).reset(from, to);

        // Assert that the current state is updated correctly
        assertEquals(restStateMock, stateMachine.getCurrentState());
    }

    /**
     * Test that update() triggers an event when the current action finishes.
     * Ensures that EventPublisher publishes the correct game event and that state transitions.
     */
    @Test
    void testUpdateTriggersEventAndStateChange() {
        long now = System.nanoTime();

        // Simulate the action being finished for MOVE state
        when(moveStateMock.isActionFinished(now)).thenReturn(true);
        when(moveStateMock.update(now)).thenReturn(Optional.of(EPieceEvent.DONE));

        // Mock target positions returned by physics data
        pieces.Position targetPosMock = mock(pieces.Position.class);
        when(movePhysicsMock.getTargetPos()).thenReturn(targetPosMock);
        when(restPhysicsMock.getTargetPos()).thenReturn(targetPosMock);

        // Mock the static EventPublisher class
        try (MockedStatic<EventPublisher> publisherMock = mockStatic(EventPublisher.class)) {
            EventPublisher fakePublisher = mock(EventPublisher.class);
            publisherMock.when(EventPublisher::getInstance).thenReturn(fakePublisher);

            // Call update on the state machine
            stateMachine.update(now);

            // Verify that EventPublisher instance was accessed
            publisherMock.verify(EventPublisher::getInstance, atLeastOnce());

            // Verify that the proper event was published
            verify(fakePublisher).publish(eq(EGameEvent.PIECE_END_MOVED), any(GameEvent.class));

            // Assert that the current state has changed to LONG_REST
            assertEquals(restStateMock, stateMachine.getCurrentState());
        }
    }

    /**
     * Test that update() does not trigger an event if the action is not finished.
     * Ensures that the state remains unchanged.
     */
    @Test
    void testUpdateDoesNotTriggerEventIfNotFinished() {
        long now = System.nanoTime();

        // Simulate the action still in progress for MOVE state
        when(moveStateMock.isActionFinished(now)).thenReturn(false);
        when(moveStateMock.update(now)).thenReturn(Optional.empty());

        // Call update on the state machine
        stateMachine.update(now);

        // Assert that the current state remains MOVE
        assertEquals(moveStateMock, stateMachine.getCurrentState());
    }
}
