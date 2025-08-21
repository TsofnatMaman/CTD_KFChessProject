package viewUtils.board;

import interfaces.IPlayerCursor;
import pieces.Position;

import javax.swing.*;
import java.util.function.Consumer;

public class CursorController {

    private final IPlayerCursor cursor;
    private Consumer<Position> onPlayerAction;
    private final KeyBindings keys;

    public CursorController(IPlayerCursor cursor, JComponent boardComponent, KeyBindings keys, KeyManager km) {
        this.cursor = cursor;
        this.keys = keys;
        km.registerController(this);
    }

    public void handleKey(String key) {
        if (cursor == null) return;

        if (key.equals(keys.up())) {
            cursor.moveUp();
        } else if (key.equals(keys.down())) {
            cursor.moveDown();
        } else if (key.equals(keys.left())) {
            cursor.moveLeft();
        } else if (key.equals(keys.right())) {
            cursor.moveRight();
        } else if (key.equals(keys.select())) {
            if (onPlayerAction != null) onPlayerAction.accept(cursor.getPosition());
        }
    }

    public void setOnPlayerAction(Consumer<Position> handler) {
        this.onPlayerAction = handler;
    }


    public record KeyBindings(String up, String down, String left, String right, String select) {
    }

}
