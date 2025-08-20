package events;

/**
 * Enum representing different game events for the event publishing system.
 * Each event has a corresponding string value used for identification.
 */
public enum EGameEvent {

    /** Event fired when a piece starts moving. */
    PIECE_START_MOVED("pieceStartMoved"),

    /** Event fired when a piece finishes moving. */
    PIECE_END_MOVED("pieceEndMoved"),

    /** Event fired when an illegal command is attempted. */
    ILLEGAL_CMD("illegalCmd"),

    /** Event fired when a piece performs a jump. */
    PIECE_JUMP("pieceJump"),

    /** Event fired when a piece is captured. */
    PIECE_CAPTURED("pieceCaptured"),

    /** Event fired when the game starts. */
    GAME_STARTED("gameStarted"),

    /** Event fired when the game ends. */
    GAME_ENDED("gameEnded"),

    /** Event fired when the game state is updated. */
    GAME_UPDATE("gameUpdate");

    /** String value representing the event name. */
    private final String val;

    EGameEvent(String val){
        this.val = val;
    }

    /**
     * Returns the string value of the event.
     *
     * @return event name as string
     */
    public String getVal() {
        return val;
    }
}
