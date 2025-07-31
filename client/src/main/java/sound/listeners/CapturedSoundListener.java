package sound.listeners;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import sound.*;

public class CapturedSoundListener implements IEventListener {

    public CapturedSoundListener(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_CAPTURED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound("capture.wav");
    }
}
