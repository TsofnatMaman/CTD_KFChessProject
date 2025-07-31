package sound.listeners;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import sound.*;

public class JumpsSoundListener implements IEventListener {

    public JumpsSoundListener(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_JUMP, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound("jump.wav");
    }
}
