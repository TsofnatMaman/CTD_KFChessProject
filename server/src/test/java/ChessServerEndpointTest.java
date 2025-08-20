import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.ChessServerEndpoint;
import server.GameHandler;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.Mockito.*;

class ChessServerEndpointTest {

    @AfterEach
    void teardown() throws Exception {
        // restore original static gameHandler instance to avoid side-effects
        Field f = ChessServerEndpoint.class.getDeclaredField("gameHandler");
        f.setAccessible(true);
        // create new real instance
        f.set(null, new GameHandler());
    }

    @Test
    void endpoint_delegatesAllCallsToGameHandler() throws Exception {
        // arrange
        GameHandler mockHandler = mock(GameHandler.class);

        // replace private static final field gameHandler with our mock via reflection
        Field field = ChessServerEndpoint.class.getDeclaredField("gameHandler");
        field.setAccessible(true);
        // remove final modifier
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, mockHandler);

        ChessServerEndpoint endpoint = new ChessServerEndpoint();

        Session s = mock(Session.class);
        when(s.getId()).thenReturn("sid");

        // onOpen
        endpoint.onOpen(s);
        verify(mockHandler).handleOpen(s);

        // onMessage
        String testMsg = "{\"type\":\"WAIT\",\"data\":\"hi\"}";
        endpoint.onMessage(testMsg, s);
        verify(mockHandler).handleMessage(eq(testMsg), eq(s));

        // onClose
        endpoint.onClose(s, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "bye"));
        verify(mockHandler).handleClose(eq(s), any());

        // onError
        Throwable t = new RuntimeException("boom");
        endpoint.onError(s, t);
        verify(mockHandler).handleError(eq(s), eq(t));
    }
}
