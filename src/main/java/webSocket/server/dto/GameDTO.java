package webSocket.server.dto;

import interfaces.IGame;

import java.util.Arrays;

public class GameDTO {
    public BoardDTO board;
    public PlayerDTO[] players;
    public long startTimeNano;
    public boolean isRun;

    public static GameDTO from(IGame game){
        GameDTO gameDTO = new GameDTO();

        gameDTO.board = BoardDTO.from(game.getBoard());
        gameDTO.players = Arrays.stream(game.getPlayers())
                .map(PlayerDTO::from)
                .toArray(PlayerDTO[]::new);
        gameDTO.startTimeNano = game.getElapsedTimeNano();
        gameDTO.isRun = game.isRunning();

        return gameDTO;
    }
}
