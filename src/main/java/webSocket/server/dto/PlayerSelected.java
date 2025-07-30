package webSocket.server.dto;

import pieces.Position;

public class PlayerSelected {
    private int playerId;
    private Position selection;

    public PlayerSelected() {}  // ריק לבנייה ע"י Jackson

    public PlayerSelected(int playerId, Position selection){
        this.playerId = playerId;
        this.selection = selection;
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public Position getSelection() { return selection; }
    public void setSelection(Position selection) { this.selection = selection; }
}
