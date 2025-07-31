package events.listeners;

import events.*;

public class JumpsLogger implements IEventListener {

    public JumpsLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_JUMP, this);
    }

    @Override
    public void onEvent(GameEvent event) {

    }
}
