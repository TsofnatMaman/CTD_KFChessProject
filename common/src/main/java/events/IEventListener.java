package events;

/**
 * Interface for classes that want to listen to game events.
 */
public interface IEventListener {

    /**
     * Called when a game event occurs.
     *
     * @param event the event that occurred
     */
    void onEvent(GameEvent event);
}
