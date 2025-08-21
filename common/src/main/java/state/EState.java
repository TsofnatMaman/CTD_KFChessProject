package state;

/**
 * Enum representing possible states for a piece.
 */
public enum EState {

    /** Idle state: can perform actions, can be captured or moved over. */
    IDLE("idle", true, true),

    /** Jump state: cannot perform actions, cannot be captured or moved over. */
    JUMP("jump", false, false),

    /** Move state: cannot perform actions, cannot be captured or moved over. */
    MOVE("move", false, false),

    /** Long rest state: cannot perform actions, can be captured or moved over. */
    LONG_REST("long_rest", false, true),

    /** Short rest state: cannot perform actions, can be captured or moved over. */
    SHORT_REST("short_rest", false, true);

    private final String name;
    private final boolean canAction;
    private final boolean canCapturable;

    /**
     * Constructs an EState enum value.
     *
     * @param name          State name
     * @param canAction     Whether actions can be performed in this state
     * @param canCapturable Whether pieces can be captured or moved over in this state
     */
    EState(String name, boolean canAction, boolean canCapturable) {
        this.name = name;
        this.canAction = canAction;
        this.canCapturable = canCapturable;
    }

    /** Returns the state name as a string. */
    @Override
    public String toString() {
        return name;
    }

    /** Returns true if actions are allowed in this state. */
    public boolean isCanAction() {
        return canAction;
    }

    /** Returns true if pieces can be captured or moved over in this state. */
    public boolean isCanCapturable() {
        return canCapturable;
    }

    /**
     * Converts a string to the corresponding EState value (case-insensitive).
     *
     * @param s State name string
     * @return EState value
     */
    public static EState getValueOf(String s) {
        return EState.valueOf(s.toUpperCase());
    }
}
