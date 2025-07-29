package events.listeners;

import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import events.SoundManager;

public class CapturedLogger implements IEventListener {

    public CapturedLogger(){
        EventPublisher.getInstance().subscribe(GameEvent.PIECE_CAPTURED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound("capture.wav");
    }
}
