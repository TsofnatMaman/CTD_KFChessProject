package utils;

/**
 * Utility class for logging debug messages to a file.
 * Provides a static method to append messages to the application's log file.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utility class for logging debug messages to a file.
 */
public class LogUtils {
    /**
     * Appends a debug message to the application's log file.
     * @param message the message to log
     */
    public static void logDebug(String message) {
        // Extracted log file name to config.properties
        String logFile = ConfigLoader.getConfig("log.file", "debug.log");
        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            out.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write to " + logFile + ": " + e.getMessage());
        }
    }
}
