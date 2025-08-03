package constants;

/**
 * Internal piece-related constants.
 */
public class PieceConstants {
    public static final String PIECE_MOVES_PATH_PREFIX = "pieces/";
    public static final String PIECE_MOVES_PATH_SUFFIX = "/moves";
    public static final String PIECE_MOVES_PATH_EXT = ".txt";
    public static final String COLOR_HEX_FORMAT = "#%02x%02x%02x";
    public static final String POSITION_SEPARATOR = ",";
    public static final String CONDITION_SEPARATOR = ":"; // extracted for Moves.java
    public static final int ROW_LETTER_OFFSET = 'A'; // for ConvertPiecePositionToName
    public static final int COLUMN_OFFSET = 1; // for ConvertPiecePositionToName and board column offset
}
