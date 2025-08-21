package events.listeners;

/**
 * Data object representing an action performed by a player.
 * <p>
 * This record is typically used as the payload for {@link events.GameEvent}
 * when publishing player actions via {@link events.EventPublisher}.
 * </p>
 *
 * @param playerId the ID of the player performing the action
 * @param message  descriptive message about the action
 */
public record ActionData(int playerId, String message) { }
