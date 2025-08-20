package dto;

/**
 * Generic message wrapper for events, containing the type and associated data.
 *
 * @param <T> the type of data carried by the message
 */
public record Message<T>(EventType type, T data) {}
