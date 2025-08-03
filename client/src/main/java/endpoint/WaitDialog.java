package endpoint;

import javax.swing.*;
import java.awt.*;

public class WaitDialog {

    private JDialog dialog;
    private JLabel label;
    private JProgressBar progressBar;
    private Runnable onCloseAction;

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    public synchronized void showOrUpdate(String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            internalShowOrUpdate(message);
        } else {
            SwingUtilities.invokeLater(() -> internalShowOrUpdate(message));
        }
    }

    private void internalShowOrUpdate(String message) {
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
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

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
            label.setText(wrapHtml(escapeHtml(message)));
            if (!dialog.isVisible()) {
                dialog.setVisible(true);
            }
        }
    }

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

    private static String wrapHtml(String s) {
        return "<html><div style='text-align:center;'>" + s + "</div></html>";
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }
}
