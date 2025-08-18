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
    private Map<EState, IState> mapState;
    private TransitionTable transitionTable;
    private IState currentState;

    public StateMachine(Map<EState, IState> mapState, TransitionTable transitions, EState initState){
        this.transitionTable = transitions;
        this.currentState = mapState.get(initState);
        this.mapState = mapState;
    }

    public void onEvent(EPieceEvent event, Position from, Position to){
        if(event == EPieceEvent.DONE && currentState.getName() == EState.MOVE)
            EventPublisher.getInstance().publish(EGameEvent.PIECE_END_MOVED, new GameEvent(EGameEvent.PIECE_END_MOVED, null));

        EState next = transitionTable.next(currentState.getName(), event);
        if(next != currentState.getName()) {
            currentState = mapState.get(next);
            currentState.reset(from, to);
        }
    }

    public void onEvent(EPieceEvent event){
        onEvent(event, currentState.getPhysics().getStartPos(), currentState.getPhysics().getStartPos());
    }

    public Optional<EPieceEvent> update(){
        if(currentState.isActionFinished())
            onEvent(EPieceEvent.DONE);

        Optional<EPieceEvent> event = currentState.update();
        event.ifPresent(ePieceEvent -> onEvent(ePieceEvent, currentState.getPhysics().getStartPos(), currentState.getPhysics().getTargetPos()));

        return event;
    }

    public IState getCurrentState() {
        return currentState;
    }
}
