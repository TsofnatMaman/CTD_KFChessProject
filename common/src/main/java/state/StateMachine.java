package state;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import interfaces.IState;
import pieces.EPieceEvent;
import pieces.Position;

import java.util.Map;
import java.util.Optional;

/**
 * StateMachine manages a piece's states, transitions, and event publishing.
 */
public class StateMachine {

    /** Map of EState → IState */
    private final Map<EState, IState> mapState;

    /** Transition table defining valid state changes */
    private final TransitionTable transitionTable;

    /** Currently active state */
    private IState currentState;

    /**
     * Constructs a StateMachine.
     *
     * @param mapState   Map of all states
     * @param transitions Transition table
     * @param initState  Initial state
     * @param initPos    Initial position of the piece
     */
    public StateMachine(Map<EState, IState> mapState, TransitionTable transitions, EState initState, Position initPos) {
        this.transitionTable = transitions;
        this.mapState = mapState;
        this.currentState = mapState.get(initState);
        this.currentState.reset(initPos, initPos);
    }

    /**
     * Processes a piece event and transitions to the next state.
     *
     * @param event Piece event
     * @param from  Starting position
     * @param to    Target position
     */
    public void onEvent(EPieceEvent event, Position from, Position to) {
        if (event == EPieceEvent.DONE && currentState.getName() == EState.MOVE) {
            EventPublisher.getInstance().publish(
                    EGameEvent.PIECE_END_MOVED,
                    new GameEvent(EGameEvent.PIECE_END_MOVED, null)
            );
        }

        EState next = transitionTable.next(currentState.getName(), event);
        currentState = mapState.get(next);
        currentState.reset(from, to);
    }

    /**
     * Processes an event using current state's target position for from/to.
     *
     * @param event Piece event
     */
    public void onEvent(EPieceEvent event) {
        Position target = currentState.getPhysics().getTargetPos();
        onEvent(event, target, target);
    }

    /**
     * Updates the current state, checks for finished actions, and triggers events.
     *
     * @param now Current time in nanoseconds
     */
    public void update(long now) {
        if (currentState.isActionFinished(now)) {
            onEvent(EPieceEvent.DONE);
        }

        Optional<EPieceEvent> event = currentState.update(now);
        event.ifPresent(this::onEvent);
    }

    /** Returns the currently active state. */
    public IState getCurrentState() {
        return currentState;
    }
}
