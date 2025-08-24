package events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Unit tests for EventPublisher.
 * Verifies subscription, unsubscription, and event dispatching behavior.
 */
class EventPublisherTest {

    private EventPublisher publisher;
    private IEventListener listener1;
    private IEventListener listener2;

    @BeforeEach
    void setUp() {
        publisher = EventPublisher.getInstance();
        listener1 = mock(IEventListener.class);
        listener2 = mock(IEventListener.class);

        // Clear any existing subscribers before each test
        publisher.unsubscribe(EGameEvent.PIECE_START_MOVED, listener1);
        publisher.unsubscribe(EGameEvent.PIECE_START_MOVED, listener2);
    }

    @Test
    void testSubscribeAndPublish() {
        // Subscribe two listeners
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, listener1);
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, listener2);

        GameEvent event = new GameEvent(EGameEvent.PIECE_START_MOVED, "data");
        publisher.publish(EGameEvent.PIECE_START_MOVED, event);

        // Both listeners should receive the event
        verify(listener1, times(1)).onEvent(event);
        verify(listener2, times(1)).onEvent(event);
    }

    @Test
    void testUnsubscribe() {
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, listener1);
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, listener2);

        // Unsubscribe listener1
        publisher.unsubscribe(EGameEvent.PIECE_START_MOVED, listener1);

        GameEvent event = new GameEvent(EGameEvent.PIECE_START_MOVED, "data");
        publisher.publish(EGameEvent.PIECE_START_MOVED, event);

        // listener1 should not receive the event
        verify(listener1, never()).onEvent(any());
        // listener2 should still receive it
        verify(listener2, times(1)).onEvent(event);
    }

    @Test
    void testPublishWithoutListeners() {
        // Publishing with no subscribers should not throw an exception
        GameEvent event = new GameEvent(EGameEvent.PIECE_START_MOVED, "data");
        publisher.publish(EGameEvent.PIECE_START_MOVED, event);
    }

    @Test
    void testMultipleSubscriptionsSameListener() {
        // Subscribe the same listener twice
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, listener1);
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, listener1);

        GameEvent event = new GameEvent(EGameEvent.PIECE_START_MOVED, "data");
        publisher.publish(EGameEvent.PIECE_START_MOVED, event);

        // Listener should receive the event twice
        verify(listener1, times(2)).onEvent(event);
    }

    @Test
    void testSubscribePublishThenUnsubscribeStopsEvents() {
        // Subscribe a listener and verify it receives the first event
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, listener1);
        GameEvent first = new GameEvent(EGameEvent.PIECE_START_MOVED, "first");
        publisher.publish(EGameEvent.PIECE_START_MOVED, first);
        verify(listener1, times(1)).onEvent(first);

        // Unsubscribe and ensure no further events are received
        publisher.unsubscribe(EGameEvent.PIECE_START_MOVED, listener1);
        GameEvent second = new GameEvent(EGameEvent.PIECE_START_MOVED, "second");
        publisher.publish(EGameEvent.PIECE_START_MOVED, second);
        verifyNoMoreInteractions(listener1);
    }
}
