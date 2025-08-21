package sound;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;

/**
 * Event listener that reacts to specific game events by playing
 * corresponding sound effects.
 * <p>
 * Subscribes automatically to several event types upon construction
 * and delegates sound playback to {@link SoundManager}.
 * </p>
 */
public class EventSoundListener implements IEventListener {

    /**
     * Constructs a new {@code EventSoundListener} and subscribes it
     * to relevant game events that should trigger sound effects.
     */
    public EventSoundListener() {
        EventPublisher publisher = EventPublisher.getInstance();

        // Subscribe to game events that require sound feedback
        publisher.subscribe(EGameEvent.PIECE_CAPTURED, this);
        publisher.subscribe(EGameEvent.GAME_ENDED, this);
        publisher.subscribe(EGameEvent.PIECE_JUMP, this);
        publisher.subscribe(EGameEvent.PIECE_START_MOVED, this);
    }

    /**
     * Handles incoming game events by mapping each event type
     * to a sound effect file and playing it.
     * <p>
     * The convention is: {@code eventType.getVal() + ".wav"}.
     * Example: if {@code event.type() == PIECE_CAPTURED},
     * it will attempt to play {@code "PIECE_CAPTURED.wav"}.
     * </p>
     *
     * @param event the game event that occurred
     */
    @Override
    public void onEvent(GameEvent event) {
        // Build filename based on event type
        String soundFile = event.type().getVal() + ".wav";

        // Delegate playback to SoundManager
        SoundManager.playSound(soundFile);
    }
}
