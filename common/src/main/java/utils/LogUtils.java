package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utility class for logging debug messages to a file.
 * Appends messages to the file specified in config.properties (default: debug.log).
 */
public class LogUtils {

    /**
     * Appends a debug message to the application's log file.
     *
     * @param message The message to log
     */
    public static void logDebug(String message) {
        // Get log file from config, fallback to "debug.log"
        String logFile = ConfigLoader.getConfig("log.file", "debug.log");

        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write to " + logFile + ": " + e.getMessage());
        }
    }
}
