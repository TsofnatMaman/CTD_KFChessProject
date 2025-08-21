package constants;

import java.awt.Color;

/**
 * Internal constants related to players.
 * <p>
 * Defines player colors, piece colors, and their corresponding names.
 * Used for rendering pieces and UI elements for each player.
 * </p>
 */
public class PlayerConstants {

    /** Colors representing the players (used for UI or highlighting). */
    public static final Color[] PLAYER_COLORS = new Color[]{Color.RED, Color.BLUE};

    /** Colors representing the pieces for each player. */
    public static final Color[] PIECES_COLOR = new Color[]{Color.BLACK, Color.WHITE};

    /** Names of the piece colors for each player (human-readable). */
    public static final String[] COLORS_NAME = new String[]{"Black", "White"};
}
