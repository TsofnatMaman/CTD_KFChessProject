package dto;

import pieces.Position;

/**
 * @param playerId  The ID of the player making the selection.
 * @param selection The position selected by the player.
 */
public record PlayerSelectedDTO(int playerId, Position selection) { }
