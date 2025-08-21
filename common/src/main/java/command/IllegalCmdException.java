package command;

/**
 * Exception thrown when an illegal command is attempted in the game.
 * <p>
 * Extends {@link RuntimeException} so it can be thrown during command execution
 * without requiring explicit handling.
 * </p>
 */
public class IllegalCmdException extends RuntimeException {

    /**
     * Constructs a new {@code IllegalCmdException} with the specified detail message.
     *
     * @param message the detail message describing the illegal command
     */
    public IllegalCmdException(String message) {
        super(message);
    }
}
