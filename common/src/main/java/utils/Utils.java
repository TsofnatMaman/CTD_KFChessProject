package utils;

import pieces.Position;

/**
 * Utility class for various helper functions related to the game.
 */
public class Utils {

    /**
     * Returns the standard chess notation for a given position.
     * Example: row=0, col=0 -> "A1"
     *
     * @param pos the position on the board
     * @return the position as a string in chess notation
     */
    public static String getName(Position pos) {
        // 'A' offset defined in constants
        char rowChar = (char) (pos.getRow() + constants.PieceConstants.ROW_LETTER_OFFSET);
        int colNum = pos.getCol() + constants.PieceConstants.COLUMN_OFFSET;
        return rowChar + "" + colNum;
    }

    /**
     * Formats elapsed time in milliseconds to "MM:SS".
     *
     * @param elapsedMillis elapsed time in milliseconds
     * @return formatted string in minutes and seconds
     */
    public static String formatElapsedTime(long elapsedMillis) {
        int seconds = (int) (elapsedMillis / 1000) % 60;
        int minutes = (int) (elapsedMillis / (1000 * 60));
        return String.format("%02d:%02d", minutes, seconds);
    }
}
