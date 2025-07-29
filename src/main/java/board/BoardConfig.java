package board;

import java.util.List;

/**
 * Configuration for the game board, including dimensions and tile size.
 */
public class BoardConfig {
    public final Dimension numRowsCols;
    
    public final Dimension panelSize;

    /** Size of a single tile in pixels. */
    public final double tileSize;


    public static final List<List<Integer>> rowsOfPlayer = List.of(
            List.of(0, 1), // Player 0
            List.of(6, 7)  // Player 1
    );

    public BoardConfig(Dimension numsRowsCols, Dimension panelSize) {
        this.numRowsCols = numsRowsCols;
        this.panelSize = panelSize;

        double tileW = (double) panelSize.getY() / numsRowsCols.getY();
        double tileH = (double) panelSize.getX() / numsRowsCols.getX();
        this.tileSize = Math.min(tileW, tileH);
    }

    public static int getPlayerOf(int row){
        if (rowsOfPlayer.get(0).contains(row))
            return 0;
        else
            return 1;
    }

    public boolean isInBounds(int r, int c){
        return r >= 0 && r < numRowsCols.getX() && c >= 0 && c < numRowsCols.getY();
    }
}
