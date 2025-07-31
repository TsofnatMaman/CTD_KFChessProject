package board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Configuration for the game board, including dimensions and tile size.
 */
public class BoardConfig implements Serializable {
    public final Dimension numRowsCols;
    public final Dimension panelSize;

    /** Size of a single tile in pixels. */
    public final double tileSize;

    public static final List<List<Integer>> rowsOfPlayer = List.of(
            List.of(0, 1), // Player 0
            List.of(6, 7)  // Player 1
    );

    @JsonCreator
    public BoardConfig(
            @JsonProperty("numRowsCols") Dimension numRowsCols,
            @JsonProperty("panelSize") Dimension panelSize
    ) {
        this.numRowsCols = numRowsCols;
        this.panelSize = panelSize;

        double tileW = (double) panelSize.getY() / numRowsCols.getY();
        double tileH = (double) panelSize.getX() / numRowsCols.getX();
        this.tileSize = Math.min(tileW, tileH);
    }

    public static int getPlayerOf(int row) {
        if (rowsOfPlayer.get(0).contains(row))
            return 0;
        else
            return 1;
    }

    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < numRowsCols.getX() && c >= 0 && c < numRowsCols.getY();
    }
}
