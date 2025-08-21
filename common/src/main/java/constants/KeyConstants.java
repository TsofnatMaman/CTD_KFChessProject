package constants;

import java.awt.event.KeyEvent;

public final class KeyConstants {
    private KeyConstants() {}

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String ENTER = "ENTER";
    public static final String SPACE = "SPACE";
    public static final String W = "W";
    public static final String A = "A";
    public static final String S = "S";
    public static final String D = "D";

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
