package board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Configuration for the game board, including:
 * - Grid dimensions (logical rows x columns)
 * - Panel dimensions (display size)
 * - Physics dimensions (for internal calculations/animations)
 *
 * Provides helper methods for:
 * - Determining which rows belong to each player
 * - Bounds checking
 */
public record BoardConfig(
        Dimension gridDimension,
        Dimension panelDimension,
        Dimension physicsDimension
) implements Serializable {

    /**
     * Defines the starting rows for each player.
     * For example: player 0 owns rows 0-1, player 1 owns rows 6-7.
     */
    public static final List<List<Integer>> rowsOfPlayer = List.of(
            List.of(0, 1), // Player 0
            List.of(6, 7)  // Player 1
    );

    /**
     * JSON constructor used by Jackson for serialization/deserialization.
     *
     * @param gridDimension    logical board grid dimensions (rows x cols)
     * @param panelDimension   UI panel display dimensions
     * @param physicsDimension internal physics or animation dimensions
     */
    @JsonCreator
    public BoardConfig(
            @JsonProperty("gridDimension") Dimension gridDimension,
            @JsonProperty("panelDimension") Dimension panelDimension,
            @JsonProperty("physicsDimension") Dimension physicsDimension
    ) {
        this.gridDimension = gridDimension;
        this.panelDimension = panelDimension;
        this.physicsDimension = physicsDimension;
    }

    /**
     * Returns the player index that owns the given row.
     *
     * @param row row index to check
     * @return player index (0,1,...)
     * @throws IllegalArgumentException if row is not assigned to any player
     */
    public static int getPlayerOf(int row) {
        return IntStream.range(0, rowsOfPlayer.size())
                .filter(i -> rowsOfPlayer.get(i).contains(row))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Row " + row + " does not belong to any player"
                ));
    }

    /**
     * Checks if a given row and column are within the bounds of the board grid.
     *
     * @param r row index
     * @param c column index
     * @return true if the position is within bounds; false otherwise
     */
    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < gridDimension.getWidth() && c >= 0 && c < gridDimension.getHeight();
    }
}
