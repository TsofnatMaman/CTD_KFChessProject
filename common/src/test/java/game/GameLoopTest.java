package game;

import events.EGameEvent;
import events.EventPublisher;
import events.IEventListener;
import interfaces.IGame;
import interfaces.IPlayer;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GameLoop} verifying basic loop behaviour.
 */
class GameLoopTest {

    @Test
    void runStartsLoop() throws Exception {
        IGame game = mock(IGame.class);
        when(game.isRunning()).thenReturn(false);
        when(game.win()).thenReturn(null);

        GameLoop loop = new GameLoop(game);
        loop.run();

        Field timerField = GameLoop.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(loop);

        assertNotNull(timer, "Timer should be initialized");
        assertTrue(timer.isRunning(), "Timer should start running");
        verify(game).setRunning(true);
        verify(game).setStartTimeNano(anyLong());

        timer.stop();
    }

    @Test
    void tickPublishesGameUpdate() throws Exception {
        IGame game = mock(IGame.class);
        when(game.win()).thenReturn(null);

        GameLoop loop = new GameLoop(game);

        IEventListener listener = mock(IEventListener.class);
        EventPublisher publisher = EventPublisher.getInstance();
        publisher.subscribe(EGameEvent.GAME_UPDATE, listener);

        Method tick = GameLoop.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(loop);

        verify(game).update();
        verify(listener).onEvent(argThat(event -> event.type() == EGameEvent.GAME_UPDATE));
        publisher.unsubscribe(EGameEvent.GAME_UPDATE, listener);
    }

    @Test
    void tickStopsWhenWinnerExists() throws Exception {
        IGame game = mock(IGame.class);
        IPlayer winner = mock(IPlayer.class);
        when(game.win()).thenReturn(winner);

        GameLoop loop = new GameLoop(game);

        Timer timer = new Timer(100, e -> {});
        timer.start();
        Field timerField = GameLoop.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        timerField.set(loop, timer);

        IEventListener listener = mock(IEventListener.class);
        EventPublisher publisher = EventPublisher.getInstance();
        publisher.subscribe(EGameEvent.GAME_ENDED, listener);

        Method tick = GameLoop.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(loop);

        assertFalse(timer.isRunning(), "Timer should stop when game ends");
        verify(game).setRunning(false);
        verify(listener).onEvent(argThat(event -> event.type() == EGameEvent.GAME_ENDED));
        publisher.unsubscribe(EGameEvent.GAME_ENDED, listener);
    }
}