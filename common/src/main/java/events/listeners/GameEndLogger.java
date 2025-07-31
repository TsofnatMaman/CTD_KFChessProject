package events.listeners;

import events.*;

public class GameEndLogger implements IEventListener {

    public GameEndLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
    }

    @Override
    public void onEvent(GameEvent event) {

    }
}
