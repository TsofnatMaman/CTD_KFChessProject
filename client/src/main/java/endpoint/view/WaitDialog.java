package endpoint.view;

import javax.swing.*;
import java.awt.*;

/**
 * A simple modal dialog displaying a message and an indeterminate progress bar,
 * used to indicate waiting for an opponent or other game events.
 */
public class WaitDialog {

    private JDialog dialog;
    private JLabel label;
    private JProgressBar progressBar;
    private Runnable onCloseAction;

    /**
     * Sets an action to execute when the dialog is closed.
     *
     * @param onCloseAction Runnable to run when the user closes the dialog.
     */
    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    /**
     * Shows the dialog with the given message or updates it if already visible.
     *
     * @param message The message to display.
     */
    public synchronized void showOrUpdate(String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            internalShowOrUpdate(message);
        } else {
            SwingUtilities.invokeLater(() -> internalShowOrUpdate(message));
        }
    }

    // Internal method to safely create or update the dialog on the EDT
    private void internalShowOrUpdate(String message) {
        if (dialog == null) {
            // Initialize components
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
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Handle window close event
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    if (onCloseAction != null) {
                        onCloseAction.run();
                    }
                }
            });

            dialog.setVisible(true);
        } else {
            // Update existing dialog
            label.setText(wrapHtml(escapeHtml(message)));
            if (!dialog.isVisible()) {
                dialog.setVisible(true);
            }
        }
    }

    /**
     * Closes the dialog safely from any thread.
     */
    public synchronized void close() {
        if (SwingUtilities.isEventDispatchThread()) {
            internalClose();
        } else {
            SwingUtilities.invokeLater(this::internalClose);
        }
    }

    private void internalClose() {
        if (dialog != null) {
            dialog.dispose();
            dialog = null;
            label = null;
            progressBar = null;
        }
    }

    // Helper to wrap text in HTML for center alignment
    private static String wrapHtml(String s) {
        return "<html><div style='text-align:center;'>" + s + "</div></html>";
    }

    // Escapes HTML special characters and converts newlines to <br/>
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }
}
