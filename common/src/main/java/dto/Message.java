package dto;

/**
 * Generic wrapper for event messages, containing the type and associated data.
 *
 * @param <T> Type of the payload data
 */
public record Message<T>(EventType type, T data) {}
