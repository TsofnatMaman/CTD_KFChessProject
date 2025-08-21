package events;

/**
 * Represents an event in the game system.
 * <p>
 * Each event contains the event type and associated data.
 * This record is used to transmit information via the {@link EventPublisher}.
 * </p>
 *
 * @param type the type of the game event (from {@link EGameEvent})
 * @param data the data associated with this event (can be any object, e.g., ActionData, GameDTO, etc.)
 */
public record GameEvent(EGameEvent type, Object data) { }
