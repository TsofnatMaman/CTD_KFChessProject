package utils;

import pieces.Position;

/**
 * Utility class for various helper functions related to the game.
 */
public final class Utils {

    private Utils() {} // Prevent instantiation

    /**
     * Returns the standard chess notation for a given position.
     * Example: row=0, col=0 -> "A1"
     *
     * @param pos the position on the board
     * @return the position as a string in chess notation
     * @throws IllegalArgumentException if pos is null
     */
    public static String getName(Position pos) {
        if (pos == null) throw new IllegalArgumentException("Position cannot be null");

        char rowChar = (char) (pos.getRow() + constants.PieceConstants.ROW_LETTER_OFFSET);
        int colNum = pos.getCol() + constants.PieceConstants.COLUMN_OFFSET;
        return rowChar + String.valueOf(colNum);
    }

    /**
     * Formats elapsed time in milliseconds to "MM:SS".
     *
     * @param elapsedMillis elapsed time in milliseconds
     * @return formatted string in minutes and seconds
     * @throws IllegalArgumentException if elapsedMillis is negative
     */
    public static String formatElapsedTime(long elapsedMillis) {
        if (elapsedMillis < 0) throw new IllegalArgumentException("Elapsed time cannot be negative");

        int seconds = (int) (elapsedMillis / 1000) % 60;
        int minutes = (int) (elapsedMillis / (1000 * 60));
        return String.format("%02d:%02d", minutes, seconds);
    }
}
