package events.listeners;

import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import events.SoundManager;

public class JumpsLogger implements IEventListener {

    public JumpsLogger(){
        EventPublisher.getInstance().subscribe(GameEvent.PIECE_JUMP, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound("jump.wav");
    }
}
