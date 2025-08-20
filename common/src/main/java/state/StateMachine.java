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
 * Manages the states of a chess piece, including transitions and event handling.
 */
public class StateMachine {

    /** Map of EState → IState instances */
    private final Map<EState, IState> mapState;

    /** Table defining valid transitions between states */
    private final TransitionTable transitionTable;

    /** Current active state */
    private IState currentState;

    /**
     * Constructs a StateMachine with initial states and transitions.
     *
     * @param mapState  Map of all states
     * @param transitions Transition table
     * @param initState Initial state
     * @param initPos   Initial position of the piece
     */
    public StateMachine(Map<EState, IState> mapState, TransitionTable transitions, EState initState, Position initPos) {
        this.transitionTable = transitions;
        this.mapState = mapState;
        this.currentState = mapState.get(initState);
        this.currentState.reset(initPos, initPos);
    }

    /**
     * Handles a piece event, updating state and publishing events if needed.
     *
     * @param event The piece event
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
     * Handles a piece event using the current state's target position for from/to.
     *
     * @param event The piece event
     */
    public void onEvent(EPieceEvent event) {
        Position target = currentState.getPhysics().getTargetPos();
        onEvent(event, target, target);
    }

    /**
     * Updates the current state based on elapsed time.
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

    /** Returns the current active state. */
    public IState getCurrentState() {
        return currentState;
    }
}
