package events.listeners;

import events.*;

public class CapturedLogger implements IEventListener {

    public CapturedLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_CAPTURED, this);
    }

    @Override
    public void onEvent(GameEvent event) {

    }
}
