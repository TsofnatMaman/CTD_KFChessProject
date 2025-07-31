package sound.listeners;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import sound.*;

public class GameEndSoundListener implements IEventListener {

    public GameEndSoundListener(){
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound("TADA.wav");
    }
}
