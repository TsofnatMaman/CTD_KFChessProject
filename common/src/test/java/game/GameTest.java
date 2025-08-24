package game;

import interfaces.ICommand;
import interfaces.IBoard;
import interfaces.IPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Position;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {

    private Game game;
    private IBoard board;
    private IPlayer player1;
    private IPlayer player2;

    @BeforeEach
    void setUp() {
        board = mock(IBoard.class);
        player1 = mock(IPlayer.class);
        player2 = mock(IPlayer.class);
        when(board.getPlayers()).thenReturn(new IPlayer[]{player1, player2});
        game = new Game(board, new IPlayer[]{player1, player2});
    }

    @Test
    void testGetPlayerByIdValid() {
        assertSame(player1, game.getPlayerById(0));
        assertSame(player2, game.getPlayerById(1));
    }

    @Test
    void testGetPlayerByIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> game.getPlayerById(-1));
        assertThrows(IllegalArgumentException.class, () -> game.getPlayerById(2));
    }

    @Test
    void testHandleSelectionQueuesCommand() {
        Position pos = new Position(1, 1);
        ICommand cmd = mock(ICommand.class);
        when(player1.handleSelection(board, pos)).thenReturn(Optional.of(cmd));

        game.handleSelection(player1, pos);

        // command should execute only after update
        verify(cmd, never()).execute();

        game.update();

        verify(board).updateAll();
        verify(cmd).execute();
    }

    @Test
    void testWinLogic() {
        // No player failed
        when(player1.isFailed()).thenReturn(false);
        when(player2.isFailed()).thenReturn(false);
        assertNull(game.win());

        // Player1 failed
        when(player1.isFailed()).thenReturn(true);
        when(player2.isFailed()).thenReturn(false);
        assertSame(player2, game.win());

        // Player2 failed
        when(player1.isFailed()).thenReturn(false);
        when(player2.isFailed()).thenReturn(true);
        assertSame(player1, game.win());
    }

    @Test
    void testElapsedTimeCalculations() {
        // initial start time is zero
        assertEquals(0, game.getElapsedMillis());

        long start = System.nanoTime() - TimeUnit.MILLISECONDS.toNanos(20);
        game.setStartTimeNano(start);
        assertEquals(start, game.getStartTimeNano());

        long elapsed = game.getElapsedMillis();
        assertTrue(elapsed >= 20);
        assertTrue(elapsed < 1000);
    }
}