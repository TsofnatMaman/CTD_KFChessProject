package events;

public enum EGameEvent {
    PIECE_MOVED("pieceMoved"),
    PIECE_JUMP("pieceJump"),
    PIECE_CAPTURED("pieceCaptured"),
    GAME_STARTED("gameStarted"),
    GAME_ENDED("gameEnded"),
    GAME_UPDATE("game_update");

    private String val;

    EGameEvent(String val){
        this.val = val;
    }
}
