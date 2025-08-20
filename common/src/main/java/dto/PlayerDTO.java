package dto;

import board.BoardConfig;
import interfaces.IPlayer;

import java.awt.*;

/**
 * Data Transfer Object representing a player.
 *
 * @param id the player's ID
 * @param name the player's name
 * @param colorHex the player's color in hex format (e.g., "#ff0000")
 */
public record PlayerDTO(int id, String name, String colorHex) {

    /**
     * Converts an IPlayer instance to a PlayerDTO.
     *
     * @param player the IPlayer instance
     * @return a PlayerDTO representing the player
     */
    public static PlayerDTO from(IPlayer player) {
        Color c = player.getColor();
        String colorHex = String.format(constants.PieceConstants.COLOR_HEX_FORMAT,
                c.getRed(), c.getGreen(), c.getBlue());
        return new PlayerDTO(player.getId(), player.getName(), colorHex);
    }

    /**
     * Converts a PlayerDTO back to an IPlayer instance using the PlayerFactory.
     *
     * @param playerDTO the PlayerDTO instance
     * @param bc the board configuration
     * @return an IPlayer instance
     */
    public static IPlayer to(PlayerDTO playerDTO, BoardConfig bc) {
        return player.PlayerFactory.createPlayer(playerDTO.id(), playerDTO.name(), bc);
    }
}
