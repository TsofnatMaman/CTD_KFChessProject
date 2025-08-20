package endpoint.view;

import javax.swing.*;

/**
 * Utility class to prompt the user for their name using a Swing dialog.
 */
public class AskUserName {

    /**
     * Displays a modal input dialog asking the user for their name.
     *
     * @return The entered username, "Anonymous" if blank, or null if cancelled.
     * @throws Exception If interrupted while waiting for the Swing thread.
     */
    public static String askUsername() throws Exception {
        final String[] result = new String[1];      // Stores the entered name
        final boolean[] cancelled = {false};        // Tracks if the dialog was cancelled

        Runnable prompt = () -> {
            String input = (String) JOptionPane.showInputDialog(
                    null,
                    utils.ConfigLoader.getMessage("enter.name", "Enter your name:"),
                    utils.ConfigLoader.getMessage("welcome.title", "Welcome to KFCHESS"),
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null
            );

            if (input == null) { // User cancelled the dialog
                cancelled[0] = true;
                return;
            }

            // Use default "Anonymous" if input is blank
            result[0] = input.trim().isEmpty()
                    ? utils.ConfigLoader.getMessage("anonymous.name", "Anonymous")
                    : input.trim();
        };

        // Run on the Event Dispatch Thread (EDT) for Swing safety
        if (SwingUtilities.isEventDispatchThread()) {
            prompt.run();
        } else {
            SwingUtilities.invokeAndWait(prompt);
        }

        return cancelled[0] ? null : result[0];
    }
}
