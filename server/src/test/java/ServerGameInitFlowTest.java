import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.GameHandler;

import javax.websocket.Session;
import java.lang.reflect.Field;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Validate that when two players call handleOpen + setName the GameHandler.createGame() runs and game is initialized.
 * Uses reflection to inspect GameHandler.game static.
 */
class ServerGameInitFlowTest {

    @AfterEach
    void tearDown() throws Exception {
        Field g = GameHandler.class.getDeclaredField("game");
        g.setAccessible(true);
        g.set(null, null);
    }

    @Test
    void twoPlayers_setName_triggersGameInitialization() throws Exception {
        GameHandler handler = new GameHandler();

        Session s1 = mock(Session.class);
        when(s1.getId()).thenReturn("s1");
        Session s2 = mock(Session.class);
        when(s2.getId()).thenReturn("s2");

        handler.handleOpen(s1);
        handler.handleOpen(s2);

        // send setName messages as raw JSON that GameHandler expects.
        handler.handleMessage("{\"type\":\"SET_NAME\",\"data\":\"Alice\"}", s1);
        handler.handleMessage("{\"type\":\"SET_NAME\",\"data\":\"Bob\"}", s2);

        // inspect static game field
        Field gameField = GameHandler.class.getDeclaredField("game");
        gameField.setAccessible(true);
        Object game = gameField.get(null);

        assertNotNull(game, "Game should have been created after two names provided");
    }
}
