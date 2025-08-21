package interfaces;

/**
 * Interface representing a command in the game using the Command Pattern.
 * Commands encapsulate actions that can be executed.
 */
public interface ICommand {

    /**
     * Executes the encapsulated command action.
     */
    void execute();
}
