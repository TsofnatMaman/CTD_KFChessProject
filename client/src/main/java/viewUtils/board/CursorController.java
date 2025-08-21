package viewUtils.board;

import interfaces.IPlayerCursor;
import pieces.Position;

import java.util.function.Consumer;

/**
 * Controller class responsible for handling keyboard input and moving
 * a player's cursor on the board. It also allows registering an action
 * handler when the player selects a position.
 */
public class CursorController {

    /** The player's cursor that will be moved by this controller. */
    private final IPlayerCursor cursor;

    /** Callback handler triggered when the player performs a "select" action. */
    private Consumer<Position> onPlayerAction;

    /** Key bindings that define which keys correspond to movement and selection. */
    private final KeyBindings keys;

    /**
     * Constructs a {@link CursorController} and registers it with the provided {@link KeyManager}.
     *
     * @param cursor the player cursor to be controlled
     * @param keys   the set of key bindings for controlling the cursor
     * @param km     the key manager responsible for delegating key events
     */
    public CursorController(IPlayerCursor cursor, KeyBindings keys, KeyManager km) {
        this.cursor = cursor;
        this.keys = keys;
        km.registerController(this);
    }

    /**
     * Handles a key press by comparing it against the defined key bindings
     * and moving the cursor or triggering an action.
     *
     * @param key the key that was pressed
     */
    public void handleKey(String key) {
        if (cursor == null) return; // Defensive check: no cursor available

        if (key.equals(keys.up())) {
            cursor.moveUp();
        } else if (key.equals(keys.down())) {
            cursor.moveDown();
        } else if (key.equals(keys.left())) {
            cursor.moveLeft();
        } else if (key.equals(keys.right())) {
            cursor.moveRight();
        } else if (key.equals(keys.select())) {
            // Trigger the action callback when "select" key is pressed
            if (onPlayerAction != null) {
                onPlayerAction.accept(cursor.getPosition());
            }
        }
    }

    /**
     * Registers a callback to be executed when the player performs
     * the "select" action on the current cursor position.
     *
     * @param handler the callback function (accepts the cursor's position)
     */
    public void setOnPlayerAction(Consumer<Position> handler) {
        this.onPlayerAction = handler;
    }

    /**
     * Record class representing a set of key bindings for cursor movement and selection.
     *
     * @param up     key assigned for moving up
     * @param down   key assigned for moving down
     * @param left   key assigned for moving left
     * @param right  key assigned for moving right
     * @param select key assigned for selecting a position
     */
    public record KeyBindings(String up, String down, String left, String right, String select) {
    }
}
