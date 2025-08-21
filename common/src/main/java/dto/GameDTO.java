package dto;

import board.BoardConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object representing the current game state.
 * Includes board configuration, players, player ID, and start time.
 */
public class GameDTO {

    /** Configuration of the game board. */
    @JsonProperty("boardConfig")
    private BoardConfig boardConfig;

    /** Array of players in the game. */
    @JsonProperty("players")
    private PlayerDTO[] players;

    /** ID of the current player. */
    @JsonProperty("yourId")
    private int yourId;

    /** Start time of the game in nanoseconds. */
    @JsonProperty("startTimeNano")
    private long startTimeNano;

    /** Default constructor for serialization/deserialization. */
    public GameDTO() {}

    /**
     * Constructs a GameDTO with the specified parameters.
     *
     * @param boardConfig Board configuration
     * @param players Array of players
     * @param yourId Current player ID
     * @param startTimeNano Game start time in nanoseconds
     */
    public GameDTO(BoardConfig boardConfig, PlayerDTO[] players, int yourId, long startTimeNano) {
        this.boardConfig = boardConfig;
        this.players = players;
        this.yourId = yourId;
        this.startTimeNano = startTimeNano;
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

    public long getStartTimeNano() {
        return startTimeNano;
    }

    public void setStartTimeNano(long startTimeNano) {
        this.startTimeNano = startTimeNano;
    }
}
