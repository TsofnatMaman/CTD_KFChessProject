package webSocket.server.dto;

import board.BoardConfig;
import interfaces.IPlayer;
import player.Player;

import java.awt.*;

public class PlayerDTO {
    private int id;
    private String name;
    private Color color; // אפשר גם enum או צבע בפורמט אחר

    public PlayerDTO() {}

    public PlayerDTO(int id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public static PlayerDTO from(IPlayer player){
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.name = player.getName();
        playerDTO.id =player.getId();
        playerDTO.color = player.getColor();
        return playerDTO;
    }

    public static IPlayer to(PlayerDTO playerDTO, BoardConfig bc){
        return new Player(playerDTO.name, bc);
    }
}
