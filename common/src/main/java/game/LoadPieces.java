package game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for loading initial piece codes from a CSV file into a static board matrix.
 * <p>
 * The board matrix can be accessed via {@link #board}. Empty cells are stored as {@code null}.
 * The CSV file is loaded automatically when this class is first accessed.
 */
public class LoadPieces {

    /**
     * Number of rows in the board.
     * Extracted from {@link constants.GameConstants#BOARD_ROWS}.
     */
    public static final int ROWS = constants.GameConstants.BOARD_ROWS;

    /**
     * Number of columns in the board.
     * Extracted from {@link constants.GameConstants#BOARD_COLS}.
     */
    public static final int COLS = constants.GameConstants.BOARD_COLS;

    /**
     * Static matrix holding the piece codes for the board.
     * Each cell contains a {@code String} representing the piece code or {@code null} if empty.
     */
    public static final String[][] board = new String[ROWS][COLS];

    // Static initializer: load the board from CSV when the class is loaded.
    static {
        loadFromCSV();
    }

    /**
     * Loads piece codes from a CSV file directly into the static {@link #board} matrix.
     * <p>
     * The CSV file is expected to be located at {@code /board/board.csv} in the resources,
     * or a different path can be specified via {@code piece.csv.path} in the configuration.
     * Empty cells are stored as {@code null}.
     *
     * @throws RuntimeException if the CSV file is not found or cannot be read
     */
    private static void loadFromCSV() {
        String csvResourcePath = utils.ConfigLoader.getConfig("piece.csv.path", "/board/board.csv");

        try (InputStream is = LoadPieces.class.getResourceAsStream(csvResourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("CSV file not found at path: " + csvResourcePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                int row = 0;

                while ((line = reader.readLine()) != null && row < ROWS) {
                    String[] cells = line.split(constants.PieceConstants.POSITION_SEPARATOR);

                    for (int col = 0; col < Math.min(cells.length, COLS); col++) {
                        String pieceCode = cells[col].trim();
                        board[row][col] = pieceCode.isEmpty() ? null : pieceCode;
                    }

                    row++;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load board CSV: " + e.getMessage(), e);
        }
    }
}
