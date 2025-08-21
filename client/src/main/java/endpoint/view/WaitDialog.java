package endpoint.view;

import javax.swing.*;
import java.awt.*;

/**
 * A simple modal-like dialog with a message and an indeterminate progress bar.
 * <p>
 * Typically used while waiting for an opponent or for asynchronous game events.
 * </p>
 */
public class WaitDialog {

    private JDialog dialog;            // The Swing dialog
    private JLabel label;              // Message label
    private JProgressBar progressBar;  // Indeterminate progress indicator
    private Runnable onCloseAction;    // Callback for when the dialog is closed

    /**
     * Sets an action to execute when the dialog is closed.
     *
     * @param onCloseAction Runnable to run when the user closes the dialog.
     */
    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    /**
     * Shows the dialog with the given message, or updates it if already visible.
     * <p>
     * This method is thread-safe and ensures execution on the Event Dispatch Thread (EDT).
     * </p>
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

    /**
     * Internal method for creating or updating the dialog (must run on EDT).
     *
     * @param message The message to display.
     */
    private void internalShowOrUpdate(String message) {
        if (dialog == null) {
            // Initialize label with wrapped + escaped HTML
            label = new JLabel(wrapHtml(escapeHtml(message)), SwingConstants.CENTER);

            // Initialize progress bar (indeterminate)
            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);

            // Create dialog window
            dialog = new JDialog((Frame) null, "Waiting for opponent", false);
            dialog.setLayout(new BorderLayout(10, 10));

            // Main content panel with padding
            JPanel content = new JPanel(new BorderLayout(5, 5));
            content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            content.add(label, BorderLayout.CENTER);
            content.add(progressBar, BorderLayout.SOUTH);

            dialog.getContentPane().add(content, BorderLayout.CENTER);
            dialog.setSize(360, 140);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Hook for close action
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
            // Update message in existing dialog
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

    /**
     * Internal method to dispose and cleanup resources.
     */
    private void internalClose() {
        if (dialog != null) {
            dialog.dispose();
            dialog = null;
            label = null;
            progressBar = null;
        }
    }

    /**
     * Wraps text in HTML for center alignment in JLabel.
     *
     * @param s Text to wrap
     * @return HTML-wrapped string
     */
    private static String wrapHtml(String s) {
        return "<html><div style='text-align:center;'>" + s + "</div></html>";
    }

    /**
     * Escapes HTML special characters and replaces newlines with &lt;br/&gt;.
     *
     * @param s Input text
     * @return Escaped HTML string
     */
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }
}
