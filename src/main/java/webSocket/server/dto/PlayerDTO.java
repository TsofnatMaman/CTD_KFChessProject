package webSocket.server.dto;

import interfaces.IPlayer;
import pieces.Position;
import player.Player;

import java.util.List;

public class PlayerDTO {
    public int id;
    public String name;
    public Position pending;

    public List<PieceDTO> pieces;
    public int score;
    public boolean isFailed;

    public static PlayerDTO from(IPlayer player){
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.id = player.getId();
        playerDTO.name = player.getName();
        playerDTO.pending = player.getPendingFrom();
        playerDTO.pieces = player.getPieces().stream().map(PieceDTO::from).toList();
        playerDTO.score = player.getScore();
        playerDTO.isFailed = player.isFailed();

        return playerDTO;
    }
}
