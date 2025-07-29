package events.listeners;

import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import events.SoundManager;

public class GameEndLogger implements IEventListener {

    public GameEndLogger(){
        EventPublisher.getInstance().subscribe(GameEvent.GAME_ENDED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        SoundManager.playSound("TADA.wav");
    }
}
