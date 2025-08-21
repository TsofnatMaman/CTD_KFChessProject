package viewUtils.game;

import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {
    private final JLabel timerLabel;

    public TimerPanel() {
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setLayout(new BorderLayout());
        add(timerLabel, BorderLayout.CENTER);
    }

    public void updateTimerLabel(String text) {
        timerLabel.setText(text);
    }

    public JLabel getLabel() {
        return timerLabel;
    }
}
