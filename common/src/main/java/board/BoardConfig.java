package board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Configuration for the game board, including dimensions and tile size.
 */
public record BoardConfig(Dimension gridDimension, Dimension panelDimension,
                          Dimension physicsDimension) implements Serializable {
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

    public static int getPlayerOf(int row) {
        return IntStream.range(0, rowsOfPlayer.size())
                .filter(i -> rowsOfPlayer.get(i).contains(row))
                .findFirst().getAsInt();
    }

    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < gridDimension.getWidth() && c >= 0 && c < gridDimension.getHeight();
    }
}
