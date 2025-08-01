package dto;

import board.BoardConfig;
import interfaces.IPlayer;
import player.Player;

import java.awt.*;

public class PlayerDTO {
    private int id;
    private String name;
    private String colorHex; // Use HEX (e.g., "#FF0000")

    public PlayerDTO() {}

    public PlayerDTO(int id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    // Converts from a regular IPlayer object to PlayerDTO
    public static PlayerDTO from(IPlayer player) {
        PlayerDTO dto = new PlayerDTO();
        dto.id = player.getId();
        dto.name = player.getName();

        // Assumes player.getColor() returns java.awt.Color
        Color c = player.getColor();
        // Extracted color hex format to PieceConstants
        dto.colorHex = String.format(constants.PieceConstants.COLOR_HEX_FORMAT, c.getRed(), c.getGreen(), c.getBlue());

        return dto;
    }

    // Creates a full Player object from DTO
    public static IPlayer to(PlayerDTO playerDTO, BoardConfig bc) {
        return player.PlayerFactory.createPlayer(playerDTO.getId(), playerDTO.getName(), bc);
    }
}
