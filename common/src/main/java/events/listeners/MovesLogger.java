package events.listeners;

import events.*;

public class MovesLogger implements IEventListener {

    public MovesLogger(){
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_START_MOVED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
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
