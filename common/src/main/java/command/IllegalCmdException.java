package command;

public class IllegalCmdException extends RuntimeException {
    public IllegalCmdException(String message) {
        super(message);
    }
}
