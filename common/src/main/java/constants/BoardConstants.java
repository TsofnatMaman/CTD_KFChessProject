package constants;

import utils.ConfigLoader;

public class BoardConstants {

    /** Pixel size of a single square on the board. */
    public static final int SQUARE_SIZE = 85;

    /** Number of board rows (can be overridden via config.properties). */
    public static final int BOARD_ROWS = Integer.parseInt(ConfigLoader.getConfig("board.rows", "8"));

    /** Number of board columns (can be overridden via config.properties). */
    public static final int BOARD_COLS = Integer.parseInt(ConfigLoader.getConfig("board.cols", "8"));

    public static final int BOARD_WIDTH_M = 500;

    public static final int BOARD_HEIGHT_M = 500;

    public static final int BOARD_PANEL_WIDTH = 700, BOARD_PANEL_HEIGHT = 700;
}
