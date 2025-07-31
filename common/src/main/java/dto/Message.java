package dto;

/**
 * Generic server message wrapper for WebSocket communication.
 * Used for serializing/deserializing messages with Jackson.
 * @param <T> The type of the data payload
 */
public class Message<T> {
    private String type;
    private T data;

    /**
     * Default constructor required for Jackson serialization.
     */
    public Message() {}

    /**
     * Constructs a server message with type and data.
     * @param type The message type
     * @param data The message payload
     */
    public Message(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
