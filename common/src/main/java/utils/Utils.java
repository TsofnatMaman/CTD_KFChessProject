package utils;

import pieces.Position;

public class Utils {

    /**
     * Returns the standard chess notation for a given position.
     * @param pos the position on the board
     * @return the position as a string (e.g., "A1")
     */
    public static String getName(Position pos){
        // Extracted 'A' to constant
        // extracted numeric constants and separator
        return (char)(pos.getRow() + constants.PieceConstants.ROW_LETTER_OFFSET) + "" + (pos.getCol() + constants.PieceConstants.COLUMN_OFFSET);
    }

    public static String formatElapsedTime(long elapsedMillis) {
        int seconds = (int) (elapsedMillis / 1000) % 60;
        int minutes = (int) (elapsedMillis / (1000 * 60));
        return String.format("%02d:%02d", minutes, seconds);
    }
}
