package dto;

/**
 * Enum representing the different types of events in the game.
 */
public enum EventType {
    /** Waiting for players or other actions. */
    WAIT,

    /** Game initialization event. */
    GAME_INIT,

    /** A player has made a selection on the board. */
    PLAYER_SELECTED,

    /** Player ID assignment event. */
    PLAYER_ID,

    /** Setting or updating a player's name. */
    SET_NAME,

    /** Unknown or unrecognized event type. */
    UNKNOWN
}
