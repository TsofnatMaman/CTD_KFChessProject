import org.junit.jupiter.api.Test;
import server.GameHandler;

import javax.websocket.Session;

import static org.mockito.Mockito.*;

/**
 * Ensure malformed JSON passed to GameHandler.handleMessage doesn't throw.
 */
class GameHandlerMalformedMessageTest {

    @Test
    void malformedJson_doesNotThrow() {
        GameHandler handler = new GameHandler();
        Session s = mock(Session.class);
        when(s.getId()).thenReturn("s-malformed");

        // This is not valid JSON for Message<T>
        String bad = "this is : not-json }";

        // Should not throw
        handler.handleMessage(bad, s);
    }
}
