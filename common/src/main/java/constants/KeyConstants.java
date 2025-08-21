package constants;

import java.awt.event.KeyEvent;

/**
 * Utility class defining key constants for player input.
 * <p>
 * Provides human-readable string identifiers for common keyboard keys
 * and a method to map {@link KeyEvent} key codes to these constants.
 * </p>
 */
public final class KeyConstants {

    // Private constructor to prevent instantiation
    private KeyConstants() {}

    /** Arrow key: UP */
    public static final String UP = "UP";

    /** Arrow key: DOWN */
    public static final String DOWN = "DOWN";

    /** Arrow key: LEFT */
    public static final String LEFT = "LEFT";

    /** Arrow key: RIGHT */
    public static final String RIGHT = "RIGHT";

    /** Enter key */
    public static final String ENTER = "ENTER";

    /** Space key */
    public static final String SPACE = "SPACE";

    /** W key */
    public static final String W = "W";

    /** A key */
    public static final String A = "A";

    /** S key */
    public static final String S = "S";

    /** D key */
    public static final String D = "D";

    /**
     * Maps a {@code KeyEvent} key code to its corresponding constant string.
     *
     * @param keyCode the key code from {@link KeyEvent}
     * @return the string representation of the key, or {@code null} if unmapped
     */
    public static String fromKeyCode(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP -> UP;
            case KeyEvent.VK_DOWN -> DOWN;
            case KeyEvent.VK_LEFT -> LEFT;
            case KeyEvent.VK_RIGHT -> RIGHT;
            case KeyEvent.VK_ENTER -> ENTER;
            case KeyEvent.VK_SPACE -> SPACE;
            case KeyEvent.VK_W -> W;
            case KeyEvent.VK_A -> A;
            case KeyEvent.VK_S -> S;
            case KeyEvent.VK_D -> D;
            default -> null;
        };
    }
}
