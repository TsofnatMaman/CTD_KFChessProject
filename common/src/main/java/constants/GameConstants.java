package constants;

/**
 * Internal game constants (board size, player count, etc.)
 */
public class GameConstants {
    public static final int BOARD_SIZE = 8; // logical board size (for square board)
    public static final int SQUARE_SIZE = 85; // pixel size per square
    public static final int BOARD_ROWS = Integer.parseInt(utils.ConfigLoader.getConfig("board.rows", "8")); // extracted from config.properties
    public static final int BOARD_COLS = Integer.parseInt(utils.ConfigLoader.getConfig("board.cols", "8")); // extracted from config.properties
    public static final int MAX_PLAYERS = 2; // logical limit

    // UI and game loop timers (ms)
    public static final int UI_TIMER_MS = 1000; // extracted from GamePanel
    public static final int GAME_LOOP_MS = 16; // extracted from Game.java
}
