import com.fasterxml.jackson.databind.ObjectMapper;
import endpoint.controller.GameController;
import endpoint.controller.GameHelper;
import endpoint.controller.PlayerActionHandler;
import endpoint.controller.IGameUI;
import events.EGameEvent;
import events.GameEvent;
import dto.EventType;
import dto.GameDTO;
import dto.PlayerSelectedDTO;
import interfaces.IGame;
import interfaces.IPlayer;
import pieces.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import endpoint.launch.ChessClientEndpoint;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void onPlayerId_setsId_and_notifiesListener() throws Exception {
        GameController.GameEventListener listener = mock(GameController.GameEventListener.class);
        controller.addListener(listener);

        Method m = GameController.class.getDeclaredMethod("onPlayerId", int.class);
        m.setAccessible(true);
        m.invoke(controller, 7);

        verify(listener).onPlayerId(7);
        assertEquals(7, controller.getPlayerId());
    }

    @Test
    void playInit_usesGameHelper_and_notifiesInit() throws Exception {
        GameDTO dto = mock(GameDTO.class);
        IGame model = mock(IGame.class);
        IGameUI ui = mock(IGameUI.class);
        GameController.GameEventListener listener = mock(GameController.GameEventListener.class);
        controller.addListener(listener);

        // set player id first
        Method setId = GameController.class.getDeclaredMethod("onPlayerId", int.class);
        setId.setAccessible(true);
        setId.invoke(controller, 1);

        try (MockedConstruction<GameHelper> mocked = mockConstruction(GameHelper.class,
                (mock, context) -> {
                    when(mock.createGame(any(GameDTO.class))).thenReturn(model);
                    when(mock.createGamePanel(eq(model), any())).thenReturn(ui);
                })) {
            Method playInit = GameController.class.getDeclaredMethod("playInit", GameDTO.class);
            playInit.setAccessible(true);
            playInit.invoke(controller, dto);
        }

        assertSame(model, controller.getModel());
        assertSame(ui, controller.getGamePanel());
        verify(listener).onGameInit();
    }

    @Test
    void sendPlayerSelection_sendsCommandToEndpoint() throws Exception {
        Method setId = GameController.class.getDeclaredMethod("onPlayerId", int.class);
        setId.setAccessible(true);
        setId.invoke(controller, 3);

        Position pos = new Position(2, 4);
        Method sendSel = GameController.class.getDeclaredMethod("sendPlayerSelection", Position.class);
        sendSel.setAccessible(true);
        sendSel.invoke(controller, pos);

        ArgumentCaptor<PlayerSelectedDTO> captor = ArgumentCaptor.forClass(PlayerSelectedDTO.class);
        verify(client).sendCommand(eq(EventType.PLAYER_SELECTED), captor.capture());
        PlayerSelectedDTO dto = captor.getValue();
        assertEquals(3, dto.playerId());
        assertEquals(pos, dto.selection());
    }

    @Test
    void fireEvent_addAndRemoveListeners() throws Exception {
        GameController.GameEventListener l = mock(GameController.GameEventListener.class);
        controller.addListener(l);

        Method fire = GameController.class.getDeclaredMethod("fireEvent", Consumer.class);
        fire.setAccessible(true);
        Consumer<GameController.GameEventListener> action = listener -> listener.onWaitMessage("hi");

        fire.invoke(controller, action);
        verify(l).onWaitMessage("hi");

        controller.removeListener(l);
        fire.invoke(controller, action);
        verifyNoMoreInteractions(l);
    }

    @Test
    void onEvent_gameEnded_callsOnWin() throws Exception {
        IGame model = mock(IGame.class);
        IGameUI gameUI = mock(IGameUI.class);
        Object winner = new Object();
        when(model.win()).thenReturn((IPlayer) winner);

        java.lang.reflect.Field modelField = GameController.class.getDeclaredField("model");
        modelField.setAccessible(true);
        modelField.set(controller, model);

        java.lang.reflect.Field panelField = GameController.class.getDeclaredField("gamePanel");
        panelField.setAccessible(true);
        panelField.set(controller, gameUI);

        controller.onEvent(new GameEvent(EGameEvent.GAME_ENDED, null));
        verify(gameUI).onWin((IPlayer) winner);
    }

    @Test
    void onEvent_pieceEndMoved_refreshesLegalMoves() throws Exception {
        PlayerActionHandler handler = mock(PlayerActionHandler.class);
        java.lang.reflect.Field f = GameController.class.getDeclaredField("playerActionHandler");
        f.setAccessible(true);
        f.set(controller, handler);

        controller.onEvent(new GameEvent(EGameEvent.PIECE_END_MOVED, null));
        verify(handler).refreshLegalMoves();
    }
}