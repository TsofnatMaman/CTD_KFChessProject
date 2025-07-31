package sound.listeners;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import sound.*;

public class MovesSoundListener implements IEventListener {

    public MovesSoundListener(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_MOVED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound("move.wav");
    }

}
