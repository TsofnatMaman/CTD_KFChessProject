package constants;

import utils.ConfigLoader;

/**
 * Internal game constants for board size, player count, and timers.
 * <p>
 * Most values are fixed, but some can be overridden via {@code config.properties}.
 * These constants are used throughout the game for board layout, player management, and timing.
 * </p>
 */
public class GameConstants {

    /** Logical board size (for square board). Default is 8x8. */
    public static final int BOARD_SIZE = 8;

    /** Pixel size of a single square on the board. */
    public static final int SQUARE_SIZE = 85;

    /** Number of board rows (can be overridden via config.properties). */
    public static final int BOARD_ROWS = Integer.parseInt(ConfigLoader.getConfig("board.rows", "8"));

    /** Number of board columns (can be overridden via config.properties). */
    public static final int BOARD_COLS = Integer.parseInt(ConfigLoader.getConfig("board.cols", "8"));

    /** Maximum number of players supported by the game. */
    public static final int MAX_PLAYERS = 2;

    /** UI update timer in milliseconds. Determines frequency of UI refresh. */
    public static final int UI_TIMER_MS = 1000;

    /** Game loop timer in milliseconds. Determines frequency of game logic updates. */
    public static final int GAME_LOOP_MS = 16;
}
