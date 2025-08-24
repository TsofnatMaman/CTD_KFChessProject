import constants.KeyConstants;
import org.junit.jupiter.api.Test;
import viewUtils.board.CursorController;
import viewUtils.board.KeyManager;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static org.mockito.Mockito.*;

class KeyManagerTest {

    @Test
    void keyPressedDispatchesButReleasedDoesNot() {
        JPanel panel = new JPanel();
        KeyManager km = new KeyManager(panel);
        CursorController controller = mock(CursorController.class);
        km.registerController(controller);

        KeyEvent press = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        for (KeyListener listener : panel.getKeyListeners()) {
            listener.keyPressed(press);
        }
        verify(controller).handleKey(KeyConstants.UP);

        reset(controller);
        KeyEvent release = new KeyEvent(panel, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        for (KeyListener listener : panel.getKeyListeners()) {
            listener.keyReleased(release);
        }
        verify(controller, never()).handleKey(anyString());
    }
}