package events;

/**
 * Represents an event in the game system, containing the event type and associated data.
 *
 * @param type the type of the game event
 * @param data the data associated with this event (can be any object)
 */
public record GameEvent(EGameEvent type, Object data) { }
