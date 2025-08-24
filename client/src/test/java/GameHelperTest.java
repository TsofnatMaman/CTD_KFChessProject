import board.BoardConfig;
import dto.GameDTO;
import dto.PlayerDTO;
import endpoint.controller.GameHelper;
import endpoint.controller.PlayerActionHandler;
import endpoint.view.BoardPanel;
import interfaces.IGame;
import interfaces.IPlayer;
import interfaces.IPlayerCursor;
import interfaces.IBoard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import viewUtils.game.GamePanel;
import viewUtils.game.PlayerInfoPanel;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link GameHelper} covering game and UI creation.
 */
public class GameHelperTest {

    @Test
    void createGame_initializesPlayersAndBoard() {
        BoardConfig bc = new BoardConfig(
                new Dimension(8, 8),
                new Dimension(400, 400),
                new Dimension(400, 400)
        );

        PlayerDTO[] players = {
                new PlayerDTO(0, "Alice", "#ff0000"),
                new PlayerDTO(1, "Bob", "#0000ff")
        };

        GameDTO dto = new GameDTO(bc, players, 0, 0L);

        GameHelper helper = new GameHelper(0);
        IGame game = helper.createGame(dto);

        assertNotNull(game);
        assertNotNull(game.getBoard());
        assertEquals(bc, game.getBoard().getBoardConfig());

        IPlayer[] gamePlayers = game.getPlayers();
        assertEquals(2, gamePlayers.length);
        assertEquals(0, gamePlayers[0].getId());
        assertEquals("Alice", gamePlayers[0].getName());
        assertEquals(1, gamePlayers[1].getId());
        assertEquals("Bob", gamePlayers[1].getName());
    }

    @Test
    void createGamePanel_buildsGamePanelWithCursorAndPlayers() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping GUI test in headless environment");

        IPlayer p0 = mock(IPlayer.class);
        when(p0.getId()).thenReturn(0);
        when(p0.getName()).thenReturn("Alice");
        when(p0.getColor()).thenReturn(Color.RED);

        IPlayer p1 = mock(IPlayer.class);
        when(p1.getId()).thenReturn(1);
        when(p1.getName()).thenReturn("Bob");
        when(p1.getColor()).thenReturn(Color.BLUE);

        IBoard board = mock(IBoard.class);
        BoardConfig bc = new BoardConfig(
                new Dimension(8, 8),
                new Dimension(400, 400),
                new Dimension(400, 400)
        );
        when(board.getBoardConfig()).thenReturn(bc);

        IGame model = mock(IGame.class);
        when(model.getPlayerById(0)).thenReturn(p0);
        when(model.getBoard()).thenReturn(board);
        when(model.getPlayers()).thenReturn(new IPlayer[]{p0, p1});

        PlayerActionHandler handler = mock(PlayerActionHandler.class);

        GameHelper helper = new GameHelper(0);

        AtomicReference<GamePanel> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set((GamePanel) helper.createGamePanel(model, handler)));

        GamePanel panel = ref.get();
        assertNotNull(panel);
        assertTrue(panel.getBoardPanel() instanceof BoardPanel);

        BoardPanel bp = (BoardPanel) panel.getBoardPanel();
        Field cursorField = BoardPanel.class.getDeclaredField("cursor");
        cursorField.setAccessible(true);
        IPlayerCursor cursor = (IPlayerCursor) cursorField.get(bp);
        assertEquals(Color.RED, cursor.getColor());

        Field pipsField = GamePanel.class.getDeclaredField("playerPanels");
        pipsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<PlayerInfoPanel> pips = (List<PlayerInfoPanel>) pipsField.get(panel);
        assertEquals(2, pips.size());

        Field playerField = PlayerInfoPanel.class.getDeclaredField("player");
        playerField.setAccessible(true);
        assertSame(p0, playerField.get(pips.get(0)));
        assertSame(p1, playerField.get(pips.get(1)));

        Color expectedBg = new Color(255, 255, 255, 180);
        assertEquals(expectedBg, pips.get(0).getBackground());
        assertEquals(expectedBg, pips.get(1).getBackground());
    }
}