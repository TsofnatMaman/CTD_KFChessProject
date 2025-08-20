package constants;

import utils.ConfigLoader;

/**
 * Internal game constants for board size, player count, and timers.
 * Values are mostly fixed but some can be overridden via config.properties.
 */
public class GameConstants {

    /** Logical board size (for square board). */
    public static final int BOARD_SIZE = 8;

    /** Pixel size per square. */
    public static final int SQUARE_SIZE = 85;

    /** Number of board rows (can be overridden via config.properties). */
    public static final int BOARD_ROWS = Integer.parseInt(ConfigLoader.getConfig("board.rows", "8"));

    /** Number of board columns (can be overridden via config.properties). */
    public static final int BOARD_COLS = Integer.parseInt(ConfigLoader.getConfig("board.cols", "8"));

    /** Maximum number of players supported by the game. */
    public static final int MAX_PLAYERS = 2;

    /** UI update timer in milliseconds. */
    public static final int UI_TIMER_MS = 1000;

    /** Game loop timer in milliseconds. */
    public static final int GAME_LOOP_MS = 16;
}
