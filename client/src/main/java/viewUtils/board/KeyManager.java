package viewUtils.board;

import java.util.ArrayList;
import java.util.List;

public class KeyManager {

    private final List<CursorController> controllers = new ArrayList<>();

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
