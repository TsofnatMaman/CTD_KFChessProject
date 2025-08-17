package endpoint.view;

import javax.swing.*;

public class AskUserName {
    /**
     * Ask the user for their name, or null if cancelled.
     */
    public static String askUsername() throws Exception {
        final String[] result = new String[1];
        final boolean[] cancelled = {false};

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
            if (input == null) { // Cancel or close
                cancelled[0] = true;
                return;
            }
            result[0] = input.trim().isEmpty()
                    ? utils.ConfigLoader.getMessage("anonymous.name", "Anonymous")
                    : input.trim();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            prompt.run();
        } else {
            SwingUtilities.invokeAndWait(prompt);
        }

        return cancelled[0] ? null : result[0];
    }
}
