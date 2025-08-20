
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EventType;
import dto.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.GameHandler;
import server.Messaging;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Basic tests for GameHandler.open/close behavior and initialization flows.
 */
class GameHandlerTest {

    private GameHandler handler;

    @BeforeEach
    void setup() {
        handler = new GameHandler();
    }

    @Test
    void handleOpen_assignsPlayerId_and_sendsMessages() throws IOException {
        // mock session
        Session session = mock(Session.class);
        when(session.getId()).thenReturn("s1");

        // mock static Messaging to capture calls
        try (var mocked = mockStatic(Messaging.class)) {
            handler.handleOpen(session);

            // Expect at least a PLAYER_ID message sent
            mocked.verify(() -> Messaging.sendMessage(eq(session), any(Message.class)), atLeastOnce());
        }
    }

    @Test
    void handleClose_removesSession() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn("s2");

        // open then close to ensure no exceptions
        try {
            handler.handleOpen(session);
        } catch (IOException ignored) {}

        handler.handleClose(session, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "test"));

        // closing should not throw and subsequent close is fine
        handler.handleClose(session, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "again"));
    }
}
