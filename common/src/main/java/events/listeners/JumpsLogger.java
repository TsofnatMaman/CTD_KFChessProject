package events.listeners;

import events.*;
import interfaces.AppLogger;
import utils.Slf4jAdapter;

public class JumpsLogger implements IEventListener {

    private static final AppLogger logger = new Slf4jAdapter(JumpsLogger.class);

    public JumpsLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_JUMP, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        logger.debug(event.toString());
    }
}
