package board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Configuration for the game board, including dimensions for grid, panel, and physics.
 * Provides helper methods to determine player starting rows and bounds checking.
 */
public record BoardConfig(
        Dimension gridDimension,
        Dimension panelDimension,
        Dimension physicsDimension
) implements Serializable {

    /** Defines which rows belong to each player. */
    public static final List<List<Integer>> rowsOfPlayer = List.of(
            List.of(0, 1), // Player 0
            List.of(6, 7)  // Player 1
    );

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
     * Returns the player index for a given row.
     *
     * @param row the row index
     * @return player index (0,1,...)
     * @throws IllegalArgumentException if row is not assigned to any player
     */
    public static int getPlayerOf(int row) {
        return IntStream.range(0, rowsOfPlayer.size())
                .filter(i -> rowsOfPlayer.get(i).contains(row))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Row " + row + " does not belong to any player"));
    }

    /**
     * Checks if the given row and column are within the bounds of the grid.
     *
     * @param r row index
     * @param c column index
     * @return true if in bounds, false otherwise
     */
    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < gridDimension.getWidth() && c >= 0 && c < gridDimension.getHeight();
    }
}
