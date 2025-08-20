package state;

/**
 * Enum representing possible states for a piece.
 */
public enum EState {

    /** Idle state: can perform actions, cannot move over other pieces. */
    IDLE("idle", true, true),

    /** Jump state: cannot perform actions, cannot move over. */
    JUMP("jump", false, false),

    /** Move state: cannot perform actions, cannot move over. */
    MOVE("move", false, false),

    /** Long rest state: cannot perform actions, can move over. */
    LONG_REST("long_rest", false, true),

    /** Short rest state: cannot perform actions, can move over. */
    SHORT_REST("short_rest", false, true);

    private final String name;
    private final boolean canAction;
    private final boolean canCapturable;

    /**
     * Constructs an EState enum value.
     *
     * @param name          State name
     * @param canAction     Whether the state allows performing actions
     * @param canCapturable Whether pieces can be captured or moved over in this state
     */
    EState(String name, boolean canAction, boolean canCapturable) {
        this.name = name;
        this.canAction = canAction;
        this.canCapturable = canCapturable;
    }

    /**
     * Returns the string name of the state.
     *
     * @return State name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns true if this state allows performing actions.
     *
     * @return true if actions are allowed
     */
    public boolean isCanAction() {
        return canAction;
    }

    /**
     * Returns true if this state allows pieces to be captured or moved over.
     *
     * @return true if capturable/movable over
     */
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
