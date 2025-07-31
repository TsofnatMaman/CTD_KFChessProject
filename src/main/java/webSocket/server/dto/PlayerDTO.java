package webSocket.server.dto;

import board.BoardConfig;
import interfaces.IPlayer;
import player.Player;

import java.awt.*;

public class PlayerDTO {
    private int id;
    private String name;
    private String colorHex; // נשתמש ב-HEX (למשל "#FF0000")

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

    // ממיר מאובייקט שחקן DTO רגיל
    public static PlayerDTO from(IPlayer player) {
        PlayerDTO dto = new PlayerDTO();
        dto.id = player.getId();
        dto.name = player.getName();

        // נניח של-player.getColor() מחזיר java.awt.Color
        Color c = player.getColor();
        dto.colorHex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());

        return dto;
    }

    // יוצר אובייקט Player מלא מתוך DTO
    public static IPlayer to(PlayerDTO playerDTO, BoardConfig bc) {
        return new Player(playerDTO.name, bc);
    }
}
