package sound;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;

/**
 * Listens to game events and plays corresponding sound effects.
 */
public class EventSoundListener implements IEventListener {

    /**
     * Constructor subscribes this listener to relevant game events.
     */
    public EventSoundListener() {
        EventPublisher publisher = EventPublisher.getInstance();

        // Subscribe to events that require sound effects
        publisher.subscribe(EGameEvent.PIECE_CAPTURED, this);
        publisher.subscribe(EGameEvent.GAME_ENDED, this);
        publisher.subscribe(EGameEvent.PIECE_JUMP, this);
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, this);
    }

    /**
     * Called when a subscribed event occurs.
     * Plays a sound file corresponding to the event type.
     *
     * @param event The game event
     */
    @Override
    public void onEvent(GameEvent event) {
        // Example: PIECE_CAPTURED -> "PIECE_CAPTURED.wav"
        String soundFile = event.type().getVal() + ".wav";
        SoundManager.playSound(soundFile);
    }
}
