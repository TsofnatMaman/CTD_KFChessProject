package game;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for loading initial piece codes from a CSV file into a static board matrix.
 */
public class LoadPieces {

    /** Number of rows in the board. */
    public static final int ROWS = 8;
    /** Number of columns in the board. */
    public static final int COLS = 8;

    /** Static matrix holding the piece codes for the board. */
    public static final String[][] board = new String[ROWS][COLS];

    // Static initializer to load the board from CSV.
    static {
        loadFromCSV();
    }

    /**
     * Loads piece codes from a CSV file directly into the static board matrix.
     * The CSV is expected to be located at /board/board.csv in the resources.
     */
    private static void loadFromCSV() {
        String csvResourcePath = "/board/board.csv";

        try (InputStream is = LoadPieces.class.getResourceAsStream(csvResourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int row = 0;

            while ((line = reader.readLine()) != null && row < ROWS) {
                String[] cells = line.split(",");
                for (int col = 0; col < Math.min(cells.length, COLS); col++) {
                    String pieceCode = cells[col].trim();
                    if (!pieceCode.isEmpty()) {
                        board[row][col] = pieceCode;
                    } else {
                        board[row][col] = null;
                    }
                }
                row++;
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns the static matrix of piece codes for the board.
     *
     * @return 2D array of piece codes
     */
    public static String[][] getBoardMatrix() {
        return board;
    }
}
