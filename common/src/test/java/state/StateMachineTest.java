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

    private IState moveStateMock;
    private IState restStateMock;
    private IPhysicsData movePhysicsMock;
    private IPhysicsData restPhysicsMock;
    private StateMachine stateMachine;

    @BeforeEach
    void setUp() {
        moveStateMock = mock(IState.class);
        restStateMock = mock(IState.class);
        movePhysicsMock = mock(IPhysicsData.class);
        restPhysicsMock = mock(IPhysicsData.class);

        when(moveStateMock.getName()).thenReturn(EState.MOVE);
        when(restStateMock.getName()).thenReturn(EState.LONG_REST);
        when(moveStateMock.getPhysics()).thenReturn(movePhysicsMock);
        when(restStateMock.getPhysics()).thenReturn(restPhysicsMock);

        Map<EState, IState> stateMap = Map.of(
                EState.MOVE, moveStateMock,
                EState.LONG_REST, restStateMock
        );

        TransitionTable transitionsMock = mock(TransitionTable.class);
        when(transitionsMock.next(EState.MOVE, EPieceEvent.DONE)).thenReturn(EState.LONG_REST);
        when(transitionsMock.next(EState.LONG_REST, EPieceEvent.DONE)).thenReturn(EState.MOVE);

        stateMachine = new StateMachine(stateMap, transitionsMock, EState.MOVE, mock(pieces.Position.class));
    }

    /**
     * onEvent should switch to the target state and invoke reset on the new state.
     */
    @Test
    void testOnEventTransitionsState() {
        pieces.Position from = mock(pieces.Position.class);
        pieces.Position to = mock(pieces.Position.class);

        stateMachine.onEvent(EPieceEvent.DONE, from, to);

        verify(restStateMock).reset(from, to);
        assertEquals(restStateMock, stateMachine.getCurrentState());
    }

    /**
     * When MOVE finishes with DONE, PIECE_END_MOVED must be published and the state must change.
     */
    @Test
    void testUpdatePublishesPieceEndMoved() {
        long now = System.nanoTime();
        when(moveStateMock.isActionFinished(now)).thenReturn(true);
        when(moveStateMock.update(now)).thenReturn(Optional.of(EPieceEvent.DONE));

        pieces.Position targetPosMock = mock(pieces.Position.class);
        when(movePhysicsMock.getTargetPos()).thenReturn(targetPosMock);
        when(restPhysicsMock.getTargetPos()).thenReturn(targetPosMock);

        try (MockedStatic<EventPublisher> publisherMock = mockStatic(EventPublisher.class)) {
            EventPublisher fakePublisher = mock(EventPublisher.class);
            publisherMock.when(EventPublisher::getInstance).thenReturn(fakePublisher);

            stateMachine.update(now);

            verify(fakePublisher).publish(eq(EGameEvent.PIECE_END_MOVED), any(GameEvent.class));
            assertEquals(restStateMock, stateMachine.getCurrentState());
        }
    }

    /**
     * If the action is not finished, update() should not change state.
     */
    @Test
    void testUpdateDoesNotTriggerEventIfNotFinished() {
        long now = System.nanoTime();
        when(moveStateMock.isActionFinished(now)).thenReturn(false);
        when(moveStateMock.update(now)).thenReturn(Optional.empty());

        stateMachine.update(now);

        assertEquals(moveStateMock, stateMachine.getCurrentState());
    }
}
