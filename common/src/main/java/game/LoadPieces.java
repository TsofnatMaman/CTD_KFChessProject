package game;

import java.io.*;

/**
 * Utility class to load initial piece codes from a CSV file into a static board matrix.
 * <p>
 * The matrix is accessible via {@link #board}. Empty cells are stored as {@code null}.
 * The CSV file is loaded automatically when this class is first accessed.
 */
public class LoadPieces {

    /**
     * Number of rows on the board.
     */
    public static final int ROWS = constants.BoardConstants.BOARD_ROWS;

    /**
     * Number of columns on the board.
     */
    public static final int COLS = constants.BoardConstants.BOARD_COLS;

    /**
     * Static matrix holding piece codes for the board.
     */
    public static final String[][] board = new String[ROWS][COLS];

    // Load board CSV when the class is loaded
    static {
        loadFromCSV();
    }

    /**
     * Loads piece codes from a CSV into the {@link #board} matrix.
     * <p>
     * The CSV is expected at {@code /board/board.csv} or overridden via configuration
     * key {@code piece.csv.path}. Empty cells are stored as {@code null}.
     *
     * @throws RuntimeException if the CSV file cannot be found or read
     */
    private static void loadFromCSV() {
        String csvResourcePath = utils.ConfigLoader.getConfig("piece.csv.path", "/board/board.csv");

        try (InputStream is = LoadPieces.class.getResourceAsStream(csvResourcePath)) {
            if (is == null) throw new FileNotFoundException("CSV file not found at path: " + csvResourcePath);

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

        } catch (IOException e) {
            throw new RuntimeException("Failed to load board CSV: " + e.getMessage(), e);
        }
    }
}
