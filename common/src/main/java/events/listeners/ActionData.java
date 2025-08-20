package events.listeners;

/**
 * Data object representing an action performed by a player.
 *
 * @param playerId the ID of the player performing the action
 * @param message descriptive message about the action
 */
public record ActionData(int playerId, String message) { }
