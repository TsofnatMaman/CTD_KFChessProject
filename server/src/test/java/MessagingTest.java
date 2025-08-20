
import dto.Message;
import org.junit.jupiter.api.Test;
import server.Messaging;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.mockito.Mockito.*;

/**
 * Tests for the Messaging helper.
 */
class MessagingTest {

    @Test
    void sendMessage_noException_whenSessionClosedOrNull() {
        // null session -> no exception
        Messaging.sendMessage(null, new Message<>(null, null));

        Session closed = mock(Session.class);
        when(closed.isOpen()).thenReturn(false);

        // closed session -> no exception
        Messaging.sendMessage(closed, new Message<>(null, null));
    }

    @Test
    void sendMessage_callsRemoteSendText_whenOpen() throws Exception {
        Session session = mock(Session.class);
        RemoteEndpoint.Basic remote = mock(RemoteEndpoint.Basic.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getBasicRemote()).thenReturn(remote);

        Message<String> msg = new Message<>(dto.EventType.WAIT, "hello");

        Messaging.sendMessage(session, msg);

        // verify remote sendText called (serialization may happen)
        verify(remote, atLeastOnce()).sendText(anyString());
    }

    @Test
    void broadcastMessage_iteratesSessions() {
        Session s1 = mock(Session.class);
        when(s1.isOpen()).thenReturn(false);

        // should not throw
        Messaging.broadcastMessage(java.util.Set.of(s1), new Message<>(dto.EventType.WAIT, "x"));
    }
}
