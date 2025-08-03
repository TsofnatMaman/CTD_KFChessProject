package events;

public enum EGameEvent {
    // Extracted event names to CommandNames
    PIECE_MOVED("pieceMoved"),
    PIECE_JUMP("pieceJump"),
    PIECE_CAPTURED("pieceCaptured"),
    GAME_STARTED("gameStarted"),
    GAME_ENDED("gameEnded"),
    GAME_UPDATE("gameUpdate");

    private final String val;

    EGameEvent(String val){
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
