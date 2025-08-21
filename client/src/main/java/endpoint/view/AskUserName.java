package endpoint.view;

import javax.swing.*;

/**
 * Utility class responsible for prompting the user for their username
 * via a Swing input dialog.
 */
public class AskUserName {

    /**
     * Displays a modal input dialog asking the user for their name.
     *
     * <p>The dialog runs on the Swing Event Dispatch Thread (EDT) for thread safety.
     * If the user cancels, this method returns {@code null}. If the user leaves
     * the input blank, the default name "Anonymous" is used.</p>
     *
     * @return The entered username, "Anonymous" if left blank, or {@code null} if cancelled.
     * @throws Exception If interrupted while waiting for the Swing thread to execute.
     */
    public static String askUsername() throws Exception {
        final String[] result = new String[1];   // Stores the entered name
        final boolean[] cancelled = {false};     // Tracks if the dialog was cancelled

        // Runnable to safely show the input dialog on the EDT
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

            if (input == null) {
                // User cancelled the dialog
                cancelled[0] = true;
                return;
            }

            // Use default "Anonymous" if input is blank, otherwise trim spaces
            result[0] = input.trim().isEmpty()
                    ? utils.ConfigLoader.getMessage("anonymous.name", "Anonymous")
                    : input.trim();
        };

        // Ensure the dialog is executed on the Event Dispatch Thread
        if (SwingUtilities.isEventDispatchThread()) {
            prompt.run();
        } else {
            SwingUtilities.invokeAndWait(prompt);
        }

        return cancelled[0] ? null : result[0];
    }
}
