package events.listeners;

import events.*;
import interfaces.AppLogger;
import utils.Slf4jAdapter;

public class MovesLogger implements IEventListener {

    private static final AppLogger logger = new Slf4jAdapter(MovesLogger.class);

    public MovesLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_START_MOVED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        logger.debug(event.toString());
        // publish(event);
    }

//    static Map<Integer, IEventListener> listeners = new HashMap<>();
//
//    public static void subscribe(IEventListener listener, int playerId){
//        listeners.put(playerId ,listener);
//    }
//
//    private void publish(GameEvent event){
//        listeners.get(((ActionData)event.data).playerId).onEvent(event);
//    }
}
