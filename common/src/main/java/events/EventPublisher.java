package events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton event publisher that manages event listeners and publishes events to them.
 * Supports thread-safe subscription, unsubscription, and event publishing.
 */
public class EventPublisher {

    /** Singleton instance of EventPublisher. */
    private static final EventPublisher instance = new EventPublisher();

    /** Mapping from game events to a list of their subscribers. */
    private final Map<EGameEvent, List<IEventListener>> listenersMap = new ConcurrentHashMap<>();

    /** Private constructor for singleton pattern. */
    private EventPublisher() {}

    /**
     * Returns the singleton instance of EventPublisher.
     * @return the EventPublisher instance
     */
    public static EventPublisher getInstance() {
        return instance;
    }

    /**
     * Subscribes a listener to a specific game event topic.
     * @param topic the game event to subscribe to
     * @param listener the listener to notify when the event occurs
     */
    public void subscribe(EGameEvent topic, IEventListener listener) {
        listenersMap.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Unsubscribes a listener from a specific game event topic.
     * @param topic the game event to unsubscribe from
     * @param listener the listener to remove
     */
    public void unsubscribe(EGameEvent topic, IEventListener listener) {
        List<IEventListener> listeners = listenersMap.get(topic);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                listenersMap.remove(topic);
            }
        }
    }

    /**
     * Publishes an event to all listeners subscribed to the specified topic.
     * Thread-safe iteration ensures safe publishing even during concurrent subscribe/unsubscribe.
     * @param topic the game event topic
     * @param event the event to publish
     */
    public void publish(EGameEvent topic, GameEvent event) {
        List<IEventListener> listeners = listenersMap.get(topic);
        if (listeners != null) {
            for (IEventListener listener : listeners) {
                listener.onEvent(event);
            }
        }
    }
}
