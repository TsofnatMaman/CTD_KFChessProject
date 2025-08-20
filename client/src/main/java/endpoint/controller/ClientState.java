package endpoint.controller;

/**
 * Enum representing the states of a client in a turn-based game.
 */
public enum ClientState {
    /** Waiting for the player to select a piece to move */
    WAIT_SELECTING_PIECE,

    /** Waiting for the player to select a target position for the chosen piece */
    WAIT_SELECTING_TARGET,
}
