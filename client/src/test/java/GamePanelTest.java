import interfaces.IBoard;
import interfaces.IPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import board.BoardConfig;
import viewUtils.BaseBoardPanel;
import viewUtils.GamePanel;
import viewUtils.PlayerInfoPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * GUI test for GamePanel.
 * - Skips in headless environments
 * - Creates real PlayerInfoPanel instances (backed by mocked IPlayer)
 * - Runs UI creation on the EDT
 */
class GamePanelTest {

    @Test
    void construct_and_updateTimer_and_onWin_doNotThrow() throws Exception {
        // Skip on headless CI machines
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping GUI test in headless environment");

        AtomicReference<GamePanel> ref = new AtomicReference<>();

        // Create UI on EDT
        SwingUtilities.invokeAndWait(() -> {
            try {
                // Create a simple BoardConfig and mocked IBoard
                BoardConfig bc = new BoardConfig(
                        new Dimension(8, 8),
                        new Dimension(400, 400),
                        new Dimension(400, 400)
                );
                IBoard board = mock(IBoard.class);
                when(board.getBoardConfig()).thenReturn(bc);

                // Concrete lightweight BaseBoardPanel (anonymous subclass)
                BaseBoardPanel bp = new BaseBoardPanel(board) {};

                // Create two real PlayerInfoPanel backed by mocked IPlayer instances
                IPlayer p1 = mock(IPlayer.class);
                when(p1.getName()).thenReturn("Player 1");
                when(p1.getId()).thenReturn(0);
                when(p1.getScore()).thenReturn(0);
                when(p1.getColor()).thenReturn(Color.BLACK);
                PlayerInfoPanel pip1 = new PlayerInfoPanel(p1);

                IPlayer p2 = mock(IPlayer.class);
                when(p2.getName()).thenReturn("Player 2");
                when(p2.getId()).thenReturn(1);
                when(p2.getScore()).thenReturn(0);
                when(p2.getColor()).thenReturn(Color.WHITE);
                PlayerInfoPanel pip2 = new PlayerInfoPanel(p2);

                // Construct GamePanel (this will call initUI safely on EDT)
                GamePanel gp = new GamePanel(bp, List.of(pip1, pip2));
                ref.set(gp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        GamePanel gp = ref.get();
        assertNotNull(gp);

        // Interact with the panel on the EDT
        SwingUtilities.invokeAndWait(() -> {
            gp.updateTimerLabel("Time: 01:23");

            // Call onWin (it creates a non-modal dialog). Use a mocked IPlayer as winner.
            IPlayer winner = mock(IPlayer.class);
            when(winner.getName()).thenReturn("Winner");
            when(winner.getId()).thenReturn(0);
            gp.onWin(winner);

            // Clean up any dialogs that might have been created during the test
            for (Window w : Window.getWindows()) {
                if (w instanceof JDialog) w.dispose();
            }
        });
    }
}
