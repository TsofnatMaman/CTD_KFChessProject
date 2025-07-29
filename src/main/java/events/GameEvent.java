package events;

public class GameEvent {
    public final EGameEvent type;
    public final Object data;

    public GameEvent(EGameEvent type, Object data) {
        this.type = type;
        this.data = data;
    }
}
