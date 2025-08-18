package state;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import interfaces.IState;
import pieces.EPieceEvent;
import pieces.Position;

import java.util.Map;
import java.util.Optional;

public class StateMachine {
    private final Map<EState, IState> mapState;
    private final TransitionTable transitionTable;
    private IState currentState;

    public StateMachine(Map<EState, IState> mapState, TransitionTable transitions, EState initState, Position initPos){
        this.transitionTable = transitions;
        this.mapState = mapState;
        this.currentState = mapState.get(initState);
        this.currentState.reset(initPos, initPos);
    }

    public void onEvent(EPieceEvent event, Position from, Position to){
        if(event == EPieceEvent.DONE && currentState.getName() == EState.MOVE)
            EventPublisher.getInstance().publish(EGameEvent.PIECE_END_MOVED, new GameEvent(EGameEvent.PIECE_END_MOVED, null));

        EState next = transitionTable.next(currentState.getName(), event);

        currentState = mapState.get(next);
        currentState.reset(from, to);
    }

    public void onEvent(EPieceEvent event){
        onEvent(event, currentState.getPhysics().getTargetPos(), currentState.getPhysics().getTargetPos());
    }

    public void update(long now){
        if(currentState.isActionFinished())
            onEvent(EPieceEvent.DONE);

        Optional<EPieceEvent> event = currentState.update(now);
        event.ifPresent(this::onEvent);
    }

    public IState getCurrentState() {
        return currentState;
    }
}
