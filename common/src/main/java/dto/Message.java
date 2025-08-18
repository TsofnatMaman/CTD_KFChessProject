package dto;

public record Message<T>(EventType type, T data) {}
