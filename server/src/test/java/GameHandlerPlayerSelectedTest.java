import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EventType;
import dto.Message;
import dto.PlayerSelectedDTO;
import interfaces.IGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.GameHandler;
import server.Messaging;

import javax.websocket.Session;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameHandlerPlayerSelectedTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws Exception {
        // Clear / reset GameHandler internal maps/lists to safe defaults
        Field sessionMapField = GameHandler.class.getDeclaredField("sessionPlayerIds");
        sessionMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentMap<Session, Integer> map = (ConcurrentMap<Session, Integer>) sessionMapField.get(null);
        map.clear();

        // playerNames list: ensure contains two names
        Field namesField = GameHandler.class.getDeclaredField("playerNames");
        namesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<String> names = (java.util.List<String>) namesField.get(null);
        names.clear();
        names.add("Alice");
        names.add("Bob");

        // Ensure game is null for these tests unless explicitly set
        Field gameField = GameHandler.class.getDeclaredField("game");
        gameField.setAccessible(true);
        gameField.set(null, null);
    }

    @Test
    void whenPlayerIdMismatch_handlePlayerSelected_ignoresAndDoesNotCallGameOrBroadcast() throws Exception {
        // Arrange
        Session s = mock(Session.class);
        when(s.getId()).thenReturn("s1");
        // Put session -> playerId mapping (0)
        Field sessionMapField = GameHandler.class.getDeclaredField("sessionPlayerIds");
        sessionMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentMap<Session, Integer> map = (ConcurrentMap<Session, Integer>) sessionMapField.get(null);
        map.put(s, 0);

        // Put a mock game (should NOT be invoked for mismatch case)
        IGame mockGame = mock(IGame.class);
        Field gameField = GameHandler.class.getDeclaredField("game");
        gameField.setAccessible(true);
        gameField.set(null, mockGame);

        // Build PLAYER_SELECTED message where playerId != mapped id
        PlayerSelectedDTO dto = new PlayerSelectedDTO(1, null);
        Message<PlayerSelectedDTO> msg = new Message<>(EventType.PLAYER_SELECTED, dto);
        String json = mapper.writeValueAsString(msg);

        // Mock Messaging static to ensure no broadcast
        try (var mockedMessaging = mockStatic(Messaging.class)) {
            // Act
            new GameHandler().handleMessage(json, s);

            // Assert: game.handleSelection should NOT be called
            verify(mockGame, never()).handleSelection(anyInt(), any());
            // Messaging.broadcastMessage should NOT be called
            mockedMessaging.verify(() -> Messaging.broadcastMessage(any(), any()), never());
        }
    }

    @Test
    void whenPlayerIdMatches_handlePlayerSelected_callsGameAndBroadcasts() throws Exception {
        // Arrange
        Session s = mock(Session.class);
        when(s.getId()).thenReturn("s2");

        Field sessionMapField = GameHandler.class.getDeclaredField("sessionPlayerIds");
        sessionMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentMap<Session, Integer> map = (ConcurrentMap<Session, Integer>) sessionMapField.get(null);
        map.put(s, 1);

        // Mock game and set to GameHandler.game
        IGame mockGame = mock(IGame.class);
        Field gameField = GameHandler.class.getDeclaredField("game");
        gameField.setAccessible(true);
        gameField.set(null, mockGame);

        PlayerSelectedDTO dto = new PlayerSelectedDTO(1, null);
        Message<PlayerSelectedDTO> msg = new Message<>(EventType.PLAYER_SELECTED, dto);
        String json = mapper.writeValueAsString(msg);

        try (var mockedMessaging = mockStatic(Messaging.class)) {
            // Act
            new GameHandler().handleMessage(json, s);

            // Assert: game.handleSelection called with playerId and selection
            verify(mockGame, times(1)).handleSelection(eq(1), any());
            // Messaging.broadcastMessage should be invoked once
            mockedMessaging.verify(() -> Messaging.broadcastMessage(any(), any()), atLeastOnce());
        }
    }
}
