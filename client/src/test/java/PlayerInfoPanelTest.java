import constants.PlayerConstants;
import interfaces.IPlayer;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import viewUtils.game.PlayerInfoPanel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerInfoPanelTest {

    @Test
    void nameColorsAndScoreUpdate() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping GUI test in headless environment");

        IPlayer player = mock(IPlayer.class);
        when(player.getName()).thenReturn("Alice");
        when(player.getId()).thenReturn(0);
        when(player.getScore()).thenReturn(3);

        SwingUtilities.invokeAndWait(() -> {
            PlayerInfoPanel panel = new PlayerInfoPanel(player);

            JPanel topPanel = (JPanel) panel.getComponent(0);
            JLabel nameLabel = (JLabel) topPanel.getComponent(0);
            JLabel scoreLabel = (JLabel) topPanel.getComponent(1);

            assertEquals("Alice", nameLabel.getText());
            assertEquals(PlayerConstants.PIECES_COLOR[0], nameLabel.getBackground());
            assertEquals(PlayerConstants.PLAYER_COLORS[0], nameLabel.getForeground());

            assertEquals("Score: 3", scoreLabel.getText());

            panel.setScore(7);
            assertEquals("Score: 7", scoreLabel.getText());
        });
    }
}