package viewUtils.game;

import javax.swing.*;
import java.awt.*;

/**
 * Panel responsible for displaying the game timer.
 * <p>
 * Shows the elapsed time in a centered label and provides
 * a method to update the displayed time.
 * </p>
 */
public class TimerPanel extends JPanel {

    /** JLabel that displays the current timer text. */
    private final JLabel timerLabel;

    /**
     * Constructs a TimerPanel with an initial time of "00:00".
     */
    public TimerPanel() {
        // Initialize label with default text
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Set layout and add the label
        setLayout(new BorderLayout());
        add(timerLabel, BorderLayout.CENTER);
    }

    /**
     * Updates the text of the timer label.
     *
     * @param text the new timer text (e.g., "Time: 02:34")
     */
    public void updateTimerLabel(String text) {
        timerLabel.setText(text);
    }

    /**
     * Returns the JLabel used for displaying the timer.
     *
     * @return the {@link JLabel} of the timer
     */
    public JLabel getLabel() {
        return timerLabel;
    }
}
