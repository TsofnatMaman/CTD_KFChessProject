package dto;

import board.BoardConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object representing the game state.
 * Contains board configuration, player information, player ID, and start time.
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
     * @param boardConfig board configuration
     * @param players array of players
     * @param yourId current player ID
     * @param startTimeNano game start time in nanoseconds
     */
    public GameDTO(BoardConfig boardConfig, PlayerDTO[] players, int yourId, long startTimeNano) {
        this.boardConfig = boardConfig;
        this.players = players;
        this.yourId = yourId;
        this.startTimeNano = startTimeNano;
    }

    public BoardConfig getBoardConfig() { return boardConfig; }
    public void setBoardConfig(BoardConfig boardConfig) { this.boardConfig = boardConfig; }

    public PlayerDTO[] getPlayers() { return players; }
    public void setPlayers(PlayerDTO[] players) { this.players = players; }

    public int getYourId() { return yourId; }
    public void setYourId(int yourId) { this.yourId = yourId; }

    public long getStartTimeNano() { return startTimeNano; }
    public void setStartTimeNano(long startTimeNano) { this.startTimeNano = startTimeNano; }
}
