package webSocket.server;

public class ServerMessage<T> {
    private String type;
    private T data;

    public ServerMessage() {
        // נדרש לסריאליזציה של Jackson
    }

    public ServerMessage(String type, T data) {
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
