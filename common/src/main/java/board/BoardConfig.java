package board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Configuration for the game board, including dimensions and tile size.
 */
public class BoardConfig implements Serializable {
    public final Dimension gridDimension;
    public final Dimension panelDimension;

    /** Size of a single tile in pixels. */
    public final double tileSize;

    public static final List<List<Integer>> rowsOfPlayer = List.of(
            List.of(0, 1), // Player 0
            List.of(6, 7)  // Player 1
    );

    @JsonCreator
    public BoardConfig(
            @JsonProperty("gridDimension") Dimension gridDimension,
            @JsonProperty("panelDimension") Dimension panelDimension
    ) {
        this.gridDimension = gridDimension;
        this.panelDimension = panelDimension;

        double tileW = (double) panelDimension.getY() / gridDimension.getY();
        double tileH = (double) panelDimension.getX() / gridDimension.getX();
        this.tileSize = Math.min(tileW, tileH);
    }

    public static int getPlayerOf(int row) {
        if (rowsOfPlayer.get(0).contains(row))
            return 0;
        else
            return 1;
    }

    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < gridDimension.getX() && c >= 0 && c < gridDimension.getY();
    }
}
