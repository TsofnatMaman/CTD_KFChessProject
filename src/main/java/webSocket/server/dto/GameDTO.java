package webSocket.server.dto;

import board.BoardConfig;

public class GameDTO {
    private BoardConfig boardConfig;
    private PlayerDTO[] players;
    private int yourId;

    // ברירת מחדל ל-Jackson
    public GameDTO() {}

    public GameDTO(BoardConfig boardConfig, PlayerDTO[] players, int yourId) {
        this.boardConfig = boardConfig;
        this.players = players;
        this.yourId = yourId;
    }

    public BoardConfig getBoardConfig() {
        return boardConfig;
    }

    public void setBoardConfig(BoardConfig boardConfig) {
        this.boardConfig = boardConfig;
    }

    public PlayerDTO[] getPlayers() {
        return players;
    }

    public void setPlayers(PlayerDTO[] players) {
        this.players = players;
    }

    public int getYourId() {
        return yourId;
    }

    public void setYourId(int yourId) {
        this.yourId = yourId;
    }
}
