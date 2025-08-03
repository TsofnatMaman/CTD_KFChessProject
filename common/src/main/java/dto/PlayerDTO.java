package dto;

/**
 * Data Transfer Object for player information.
 * Contains player ID, name, and color (in HEX format).
 */

import board.BoardConfig;
import interfaces.IPlayer;

import java.awt.*;

public class PlayerDTO {
    /** The player's ID. */
    private int id;
    /** The player's name. */
    private String name;
    /** The player's color in HEX format (e.g., "#FF0000"). */
    private String colorHex;

    /**
     * Default constructor for serialization/deserialization frameworks.
     */
    public PlayerDTO() {}

    /**
     * Constructs a PlayerDTO.
     * @param id the player ID
     * @param name the player name
     * @param colorHex the player color in HEX
     */
    public PlayerDTO(int id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }

    /**
     * Gets the player ID.
     * @return the player ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the player ID.
     * @param id the player ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the player name.
     * @return the player name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the player name.
     * @param name the player name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the player color in HEX format.
     * @return the color HEX string
     */
    public String getColorHex() {
        return colorHex;
    }

    /**
     * Sets the player color in HEX format.
     * @param colorHex the color HEX string
     */
    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    // Converts from a regular IPlayer object to PlayerDTO
    /**
     * Creates a PlayerDTO from an IPlayer instance.
     * @param player the player instance
     * @return a PlayerDTO representing the player
     */
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
    /**
     * Converts a PlayerDTO to an IPlayer instance.
     * @param playerDTO the PlayerDTO
     * @param bc the board configuration
     * @return an IPlayer instance
     */
    public static IPlayer to(PlayerDTO playerDTO, BoardConfig bc) {
        return player.PlayerFactory.createPlayer(playerDTO.getId(), playerDTO.getName(), bc);
    }
}
