package dto;

import board.BoardConfig;
import interfaces.IPlayer;

import java.awt.*;

public record PlayerDTO(int id, String name, String colorHex) {

    /**
     * Converts from a regular IPlayer object to PlayerDTO.
     */
    public static PlayerDTO from(IPlayer player) {
        Color c = player.getColor();
        String colorHex = String.format(constants.PieceConstants.COLOR_HEX_FORMAT,
                c.getRed(), c.getGreen(), c.getBlue());
        return new PlayerDTO(player.getId(), player.getName(), colorHex);
    }

    /**
     * Converts a PlayerDTO to an IPlayer instance.
     */
    public static IPlayer to(PlayerDTO playerDTO, BoardConfig bc) {
        return player.PlayerFactory.createPlayer(playerDTO.id(), playerDTO.name(), bc);
    }
}
