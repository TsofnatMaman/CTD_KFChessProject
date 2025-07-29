package events.listeners;

public class ActionData {
    public final int playerId;
    public final String message;

    public ActionData(int playerId, String message){
        this.playerId = playerId;
        this.message = message;
    }
}
