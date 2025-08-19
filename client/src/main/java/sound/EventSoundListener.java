package sound;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;

public class EventSoundListener implements IEventListener {
    public EventSoundListener(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_CAPTURED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_JUMP, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_START_MOVED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound(event.type().getVal()+".wav");
    }
}
