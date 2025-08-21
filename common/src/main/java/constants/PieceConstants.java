package constants;

/**
 * Internal constants related to game pieces.
 * <p>
 * Includes file paths for piece move definitions, formatting strings, and offsets
 * for converting board coordinates to human-readable form.
 * </p>
 */
public class PieceConstants {

    /** Path prefix for piece move files in the resources folder. */
    public static final String PIECE_MOVES_PATH_PREFIX = "pieces/";

    /** Path suffix for piece move files in the resources folder. */
    public static final String PIECE_MOVES_PATH_SUFFIX = "/moves";

    /** File extension for piece move files. */
    public static final String PIECE_MOVES_PATH_EXT = ".txt";

    /** Format string for converting RGB color values to hex format. */
    public static final String COLOR_HEX_FORMAT = "#%02x%02x%02x";

    /** Separator for position coordinates in move files (e.g., "dx,dy"). */
    public static final String POSITION_SEPARATOR = ",";

    /** Separator for move conditions in move files (e.g., "dx,dy:NON_CAPTURE"). */
    public static final String CONDITION_SEPARATOR = ":";

    /** Offset for converting row index to letter (e.g., row 0 -> 'A'). */
    public static final int ROW_LETTER_OFFSET = 'A';

    /** Offset for converting column index to number (1-based, e.g., column 0 -> 1). */
    public static final int COLUMN_OFFSET = 1;
}
