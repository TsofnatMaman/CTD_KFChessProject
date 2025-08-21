package dto;

import board.BoardConfig;
import interfaces.IPlayer;

import java.awt.*;

/**
 * Data Transfer Object representing a player.
 *
 * @param id Player's unique ID
 * @param name Player's display name
 * @param colorHex Player's color in hexadecimal format (e.g., "#ff0000")
 */
public record PlayerDTO(int id, String name, String colorHex) {

    /**
     * Converts an IPlayer instance into a PlayerDTO.
     *
     * @param player IPlayer instance
     * @return PlayerDTO representing the player
     */
    public static PlayerDTO from(IPlayer player) {
        Color c = player.getColor();
        String colorHex = String.format(constants.PieceConstants.COLOR_HEX_FORMAT,
                c.getRed(), c.getGreen(), c.getBlue());
        return new PlayerDTO(player.getId(), player.getName(), colorHex);
    }

    /**
     * Converts a PlayerDTO back into an IPlayer using the PlayerFactory.
     *
     * @param playerDTO The PlayerDTO instance
     * @param bc Board configuration
     * @return IPlayer instance
     */
    public static IPlayer to(PlayerDTO playerDTO, BoardConfig bc) {
        return player.PlayerFactory.createPlayer(playerDTO.id(), playerDTO.name(), bc);
    }
}
