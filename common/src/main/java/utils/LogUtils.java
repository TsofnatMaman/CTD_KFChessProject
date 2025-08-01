package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utility class for logging debug messages to a file.
 */
public class LogUtils {
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
