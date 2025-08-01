package utils;

import pieces.Position;

public class ConvertPiecePositionToName {

    public static String getName(Position pos){
        // Extracted 'A' to constant
        // extracted numeric constants and separator
        return (char)(pos.getRow() + constants.PieceConstants.ROW_LETTER_OFFSET) + constants.PieceConstants.POSITION_SEPARATOR + (pos.getCol() + constants.PieceConstants.COLUMN_OFFSET);
    }
}
