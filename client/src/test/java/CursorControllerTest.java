import constants.KeyConstants;
import org.junit.jupiter.api.Test;
import player.PlayerCursor;
import pieces.Position;
import viewUtils.board.CursorController;
import viewUtils.board.KeyManager;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class CursorControllerTest {

    @Test
    void cursorMovesAndClampsWithinBoard() {
        PlayerCursor cursor = new PlayerCursor(new Position(0, 0), Color.RED);
        KeyManager km = new KeyManager(new JPanel());
        CursorController.KeyBindings bindings = new CursorController.KeyBindings(
                KeyConstants.UP,
                KeyConstants.DOWN,
                KeyConstants.LEFT,
                KeyConstants.RIGHT,
                KeyConstants.ENTER
        );
        new CursorController(cursor, bindings, km);

        // Attempt to move beyond upper/left boundaries
        km.handleKey(KeyConstants.UP);
        km.handleKey(KeyConstants.LEFT);
        assertEquals(new Position(0, 0), cursor.getPosition());

        // Move right past board edge
        for (int i = 0; i < 9; i++) {
            km.handleKey(KeyConstants.RIGHT);
        }
        assertEquals(7, cursor.getPosition().getCol());

        // Move down past board edge
        for (int i = 0; i < 9; i++) {
            km.handleKey(KeyConstants.DOWN);
        }
        assertEquals(7, cursor.getPosition().getRow());
    }
}