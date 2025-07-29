package interfaces;

/**
 * Enum representing possible states for a piece.
 */
public enum EState {
    /** Idle state, can perform actions, cannot move over. */
    IDLE("idle", true, false),
    /** Jump state, cannot perform actions, cannot move over. */
    JUMP("jump", false, false),
    /** Move state, cannot perform actions, can move over. */
    MOVE("move", false, true),
    /** Long rest state, cannot perform actions, can move over. */
    LONG_REST("long_rest", false, true),
    /** Short rest state, cannot perform actions, can move over. */
    SHORT_REST("short_rest", false, true);

    private String name;
    private boolean canAction;
    private boolean canMoveOver;

    /**
     * Constructs an EState enum value.
     * @param name State name
     * @param canAction Whether the state allows actions
     * @param canMoveOver Whether the state allows moving over
     */
    EState(String name, boolean canAction, boolean canMoveOver){
        this.name = name;
        this.canAction = canAction;
        this.canMoveOver = canMoveOver;
    }

    /**
     * Returns the name of the state.
     * @return State name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns true if the state allows actions.
     * @return true if can perform actions
     */
    public boolean isCanAction(){
        return canAction;
    }

    /**
     * Returns true if the state allows moving over.
     * @return true if can move over
     */
    public boolean isCanMoveOver() {
        return canMoveOver;
    }

    /**
     * Gets the EState value from a string (case-insensitive).
     * @param s State name string
     * @return EState value
     */
    public static EState getValueOf(String s){
        return EState.valueOf(s.toUpperCase());
    }
}
