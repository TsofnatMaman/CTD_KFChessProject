package utils;

/**
 * Utility class for converting a chess board position to its standard name (e.g., A1, H8).
 */

import pieces.Position;

public class ConvertPiecePositionToName {

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
}
