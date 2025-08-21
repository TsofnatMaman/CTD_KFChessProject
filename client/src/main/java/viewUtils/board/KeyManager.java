package viewUtils.board;

import constants.KeyConstants;
import constants.Messages;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class KeyManager {

    private final List<CursorController> controllers = new ArrayList<>();


    public KeyManager(JComponent boardComponent){
        boardComponent.setFocusable(true);
        boardComponent.requestFocusInWindow();

        boardComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String key = KeyConstants.fromKeyCode(e.getKeyCode());
                if (key != null) {
                    handleKey(key);
                }
            }
        });
    }

    public void registerController(CursorController controller) {
        if (!controllers.contains(controller)) {
            controllers.add(controller);
        }
    }

    public void handleKey(String key) {
        for (CursorController controller : controllers) {
            controller.handleKey(key);
        }
    }
}
