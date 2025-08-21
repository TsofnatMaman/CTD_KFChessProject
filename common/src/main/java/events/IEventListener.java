package events;

/**
 * Interface for classes that want to listen to game events.
 * <p>
 * Implement this interface to receive notifications when specific events occur
 * in the game. Register the listener via {@link EventPublisher#subscribe(EGameEvent, IEventListener)}.
 * </p>
 */
public interface IEventListener {

    /**
     * Called when a game event occurs.
     *
     * @param event the event that occurred, containing type and associated data
     */
    void onEvent(GameEvent event);
}
