package viewUtils.board;

import constants.KeyConstants;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages keyboard input for the game board.
 * <p>
 * The {@code KeyManager} listens to key events from a given Swing component
 * (e.g., the board panel) and delegates the events to registered
 * {@link CursorController} instances. Each controller is responsible for
 * moving its cursor or performing actions based on key bindings.
 * </p>
 */
public class KeyManager {

    /** List of controllers that will receive key events. */
    private final List<CursorController> controllers = new ArrayList<>();

    /**
     * Creates a new {@code KeyManager} and attaches a key listener to the given board component.
     *
     * @param boardComponent the Swing component that represents the game board;
     *                       it will be set to focusable and receive keyboard input
     */
    public KeyManager(JComponent boardComponent) {
        boardComponent.setFocusable(true);
        boardComponent.requestFocusInWindow();

        // Add a key listener to capture and translate key events
        boardComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Convert raw key code to a logical key string (via KeyConstants)
                String key = KeyConstants.fromKeyCode(e.getKeyCode());
                if (key != null) {
                    handleKey(key);
                }
            }
        });
    }

    /**
     * Registers a {@link CursorController} to receive key events.
     * If the controller is already registered, it will not be added again.
     *
     * @param controller the controller to register
     */
    public void registerController(CursorController controller) {
        if (!controllers.contains(controller)) {
            controllers.add(controller);
        }
    }

    /**
     * Dispatches a key event to all registered {@link CursorController} instances.
     *
     * @param key the key (as a string) that was pressed
     */
    public void handleKey(String key) {
        // Notify all registered controllers of the pressed key
        for (CursorController controller : controllers) {
            controller.handleKey(key);
        }
    }
}
