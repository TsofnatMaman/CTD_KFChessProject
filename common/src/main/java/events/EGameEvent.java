package events;

public enum EGameEvent {
    // Extracted event names to CommandNames
    PIECE_MOVED(constants.CommandNames.PIECE_MOVED),
    PIECE_JUMP(constants.CommandNames.PIECE_JUMP),
    PIECE_CAPTURED(constants.CommandNames.PIECE_CAPTURED),
    GAME_STARTED(constants.CommandNames.GAME_STARTED),
    GAME_ENDED(constants.CommandNames.GAME_ENDED),
    GAME_UPDATE(constants.CommandNames.GAME_UPDATE);

    private final String val;

    EGameEvent(String val){
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
