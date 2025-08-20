package constants;

/**
 * Internal constants related to game pieces.
 * Includes file paths, formatting strings, and offsets for board positions.
 */
public class PieceConstants {

    /** Path prefix for piece move files. */
    public static final String PIECE_MOVES_PATH_PREFIX = "pieces/";

    /** Path suffix for piece move files. */
    public static final String PIECE_MOVES_PATH_SUFFIX = "/moves";

    /** File extension for piece move files. */
    public static final String PIECE_MOVES_PATH_EXT = ".txt";

    /** Hex color format string. */
    public static final String COLOR_HEX_FORMAT = "#%02x%02x%02x";

    /** Separator for position coordinates in files. */
    public static final String POSITION_SEPARATOR = ",";

    /** Separator for move conditions in files (used in Moves.java). */
    public static final String CONDITION_SEPARATOR = ":";

    /** Offset for converting row index to letter (e.g., 'A'). */
    public static final int ROW_LETTER_OFFSET = 'A';

    /** Offset for converting column index to number (1-based). */
    public static final int COLUMN_OFFSET = 1;
}
