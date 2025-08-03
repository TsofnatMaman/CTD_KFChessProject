package endpoint;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for displaying waiting messages to the user (e.g., waiting for opponent).
 */
public class WaitDialog {

    private JDialog dialog;
    private JLabel label;
    private JProgressBar progressBar;

    /**
     * Shows or updates the waiting dialog with the given message.
     * @param message The message to display
     */
    public void showOrUpdate(String message) {
        if (dialog == null) {
            label = new JLabel(wrapHtml(escapeHtml(message)), SwingConstants.CENTER);
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);

            dialog = new JDialog((Frame) null, "Waiting for opponent", false);
            dialog.setLayout(new BorderLayout(10, 10));

            JPanel content = new JPanel(new BorderLayout(5, 5));
            content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            content.add(label, BorderLayout.CENTER);
            content.add(progressBar, BorderLayout.SOUTH);

            dialog.getContentPane().add(content, BorderLayout.CENTER);
            dialog.setSize(360, 140);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setVisible(true);
        } else {
            label.setText(wrapHtml(escapeHtml(message)));
            if (!dialog.isVisible()) {
                dialog.setVisible(true);
            }
        }
    }

    /**
     * Closes the waiting dialog if open.
     */
    public void close() {
        if (dialog != null) {
            dialog.dispose();
            dialog = null;
            label = null;
            progressBar = null;
        }
    }

    private static String wrapHtml(String s) {
        // Extracted HTML wrapper to constant
        return "<html><div style='text-align:center;'>" + s + "</div></html>";
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>"); // extracted newline
    }
}
