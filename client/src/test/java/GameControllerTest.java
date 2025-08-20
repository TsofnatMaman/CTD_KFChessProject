
import com.fasterxml.jackson.databind.ObjectMapper;
import endpoint.controller.GameController;
import endpoint.controller.IGameUI;
import events.EGameEvent;
import events.GameEvent;
import interfaces.IGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import endpoint.launch.ChessClientEndpoint;

import static org.mockito.Mockito.*;

/**
 * Tests for GameController: event handling and thread lifecycle pieces.
 */
public class GameControllerTest {

    private GameController controller;
    private ChessClientEndpoint client;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        client = mock(ChessClientEndpoint.class);
        mapper = new ObjectMapper();
        controller = new GameController(client, mapper);
    }

    @Test
    void startStopListening_and_runLoop_handlesInterruptGracefully()  {
        // start listening (will create a thread that polls - but we will interrupt)
        controller.startListening();
        controller.stopListening();
        // no exceptions expected
    }

    @Test
    void onEvent_gameUpdate_callsGamePanelMethods() {
        IGame model = mock(IGame.class);
        IGameUI gameUI = mock(IGameUI.class);
        when(model.getElapsedMillis()).thenReturn(12345L);
        controller = new GameController(client, mapper);

        // inject model and panel via reflection or setter if exists — fallback: access fields via reflection
        // Here we assume settable via reflection for testing convenience.
        try {
            java.lang.reflect.Field modelField = GameController.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(controller, model);

            java.lang.reflect.Field panelField = GameController.class.getDeclaredField("gamePanel");
            panelField.setAccessible(true);
            panelField.set(controller, gameUI);

            // send update event
            controller.onEvent(new GameEvent(EGameEvent.GAME_UPDATE, null));

            verify(gameUI).onGameUpdate();
            verify(gameUI).updateTimerLabel(anyString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
