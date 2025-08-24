package events.listeners;

import events.*;
import interfaces.AppLogger;
import utils.Slf4jAdapter;

public class CapturedLogger implements IEventListener {

    private static final AppLogger logger = new Slf4jAdapter(CapturedLogger.class);

    public CapturedLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_CAPTURED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        logger.debug(event.toString());
    }
}
