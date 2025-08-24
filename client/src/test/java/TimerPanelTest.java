import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import javax.swing.*;
import java.awt.*;

import viewUtils.game.TimerPanel;

import static org.junit.jupiter.api.Assertions.*;

class TimerPanelTest {

    @Test
    void labelFormattingAndUpdate() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping GUI test in headless environment");

        SwingUtilities.invokeAndWait(() -> {
            TimerPanel panel = new TimerPanel();
            JLabel label = panel.getLabel();

            assertEquals("Time: 00:00", label.getText());
            assertEquals(SwingConstants.CENTER, label.getHorizontalAlignment());
            Font font = label.getFont();
            assertEquals("Arial", font.getName());
            assertEquals(Font.BOLD, font.getStyle());
            assertEquals(18, font.getSize());

            panel.updateTimerLabel("Time: 01:23");
            assertEquals("Time: 01:23", label.getText());
        });
    }
}