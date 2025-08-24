package interfaces;

public interface AppLogger {
    void debug(String message);
    void info(String message);
    void warn(String message);
    void error(String message, Throwable t);
}