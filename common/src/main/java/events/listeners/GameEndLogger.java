package events.listeners;

import events.*;
import interfaces.AppLogger;
import utils.Slf4jAdapter;

public class GameEndLogger implements IEventListener {

    private static final AppLogger logger = new Slf4jAdapter(GameEndLogger.class);

    public GameEndLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        logger.debug(event.toString());
    }
}
