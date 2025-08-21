package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging debug messages to a file.
 * Appends messages to the file specified in config.properties (default: debug.log).
 */
public final class LogUtils {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Prevent instantiation
    private LogUtils() {}

    /**
     * Appends a debug message to the application's log file with a timestamp.
     *
     * @param message The message to log
     */
    public static void logDebug(String message) {
        String logFile = ConfigLoader.getConfig("log.file", "debug.log");
        String timestampedMessage = String.format("[%s] %s", LocalDateTime.now().format(TIMESTAMP_FORMAT), message);

        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println(timestampedMessage);
        } catch (IOException e) {
            System.err.println("Failed to write to " + logFile + ": " + e.getMessage());
        }
    }
}
