import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import org.junit.jupiter.api.Test;
import sound.EventSoundListener;

/**
 * Smoke test for EventSoundListener to ensure registering and handling doesn't throw.
 */
class EventSoundListenerTest {

    @Test
    void eventSoundListener_registersWithoutThrowing() {
        // Constructing should subscribe to events internally
        new EventSoundListener();

        // Publish an event and ensure no exception
        EventPublisher.getInstance().publish(EGameEvent.GAME_UPDATE, new GameEvent(EGameEvent.GAME_UPDATE, null));
    }
}
