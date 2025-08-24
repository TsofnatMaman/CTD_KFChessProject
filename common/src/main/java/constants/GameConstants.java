package constants;

/**
 * Internal game constants for board size, player count, and timers.
 * <p>
 * Most values are fixed, but some can be overridden via {@code config.properties}.
 * These constants are used throughout the game for board layout, player management, and timing.
 * </p>
 */
public class GameConstants {

    /** Maximum number of players supported by the game. */
    public static final int MAX_PLAYERS = 2;

    /** UI update timer in milliseconds. Determines frequency of UI refresh. */
    public static final int UI_TIMER_MS = 1000;

    /** Game loop timer in milliseconds. Determines frequency of game logic updates. */
    public static final int GAME_LOOP_MS = 16;

    public static final long NANOS_IN_SECOND = 1_000_000_000;
}
