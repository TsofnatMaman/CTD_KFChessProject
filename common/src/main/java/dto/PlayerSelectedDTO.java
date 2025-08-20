package dto;

import pieces.Position;

/**
 * Data Transfer Object representing a player's selection on the board.
 *
 * @param playerId  ID of the player making the selection
 * @param selection The position selected by the player
 */
public record PlayerSelectedDTO(int playerId, Position selection) { }
