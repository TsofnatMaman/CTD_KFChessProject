import board.BoardConfig;
import endpoint.view.BoardPanel;
import interfaces.IBoard;
import interfaces.IPlayerCursor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import pieces.Position;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Fixed GUI test for BoardPanel:
 *  - ensures board.getBoardConfig() is not null
 *  - creates UI on the EDT
 *  - skips the test in headless environments
 */
class BoardPanelTest {

    @Test
    void construct_and_setters_doNotThrow() throws Exception {
        // skip on headless CI machines
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping GUI test in headless environment");

        AtomicReference<BoardPanel> ref = new AtomicReference<>();

        // create UI on the EDT
        SwingUtilities.invokeAndWait(() -> {
            // real BoardConfig (used by BaseBoardPanel.setPreferredSize(...))
            BoardConfig bc = new BoardConfig(
                    new Dimension(8, 8),            // gridDimension
                    new Dimension(400, 400),        // panelDimension
                    new Dimension(500, 500)         // physicsDimension (not important here)
            );

            // mock IBoard but stub getBoardConfig() to return a valid config
            IBoard board = mock(IBoard.class);
            when(board.getBoardConfig()).thenReturn(bc);
            when(board.getRows()).thenReturn(8);
            when(board.getCols()).thenReturn(8);

            // mock cursor with minimal behavior used by BoardPanel ctor
            IPlayerCursor cursor = mock(IPlayerCursor.class);
            when(cursor.getPosition()).thenReturn(new Position(0, 0));
            when(cursor.getColor()).thenReturn(Color.RED);

            // construct the panel (calls BaseBoardPanel(board) under the hood)
            BoardPanel panel = new BoardPanel(board, cursor);
            ref.set(panel);
        });

        BoardPanel panel = ref.get();
        assertNotNull(panel);

        // interact with the panel on the EDT as well
        SwingUtilities.invokeAndWait(() -> {
            panel.setSelected(new Position(1, 1));
            panel.setLegalMoves(List.of(new Position(2, 2)));
            panel.repaint();

            // preferred size should have been set from BoardConfig.panelDimension()
            assertEquals(new Dimension(400, 400), panel.getPreferredSize());
        });
    }
}
