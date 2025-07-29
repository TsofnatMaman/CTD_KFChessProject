package interfaces;

import board.BoardConfig;
import pieces.Position;

import java.util.List;

/**
 * Interface for board operations and queries.
 */
public interface IBoard {

    /**
     * Places a piece on the board.
     * @param piece The piece to place
     */
    void placePiece(IPiece piece);

    /**
     * Checks if there is a piece at the specified row and column.
     * @param row The row index
     * @param col The column index
     * @return true if a piece exists, false otherwise
     */
    boolean hasPiece(int row, int col);

    /**
     * Gets the piece at the specified row and column.
     * @param row The row index
     * @param col The column index
     * @return The piece or null
     */
    IPiece getPiece(int row, int col);

    /**
     * Gets the piece at the specified position.
     * @param pos The position object
     * @return The piece or null
     */
    IPiece getPiece(Position pos);

    /**
     * Returns the player index for a given row.
     * @param row The row index
     * @return The player index
     */
    int getPlayerOf(int row);

    /**
     * Returns the player index for a given position.
     * @param pos The position object
     * @return The player index
     */
    int getPlayerOf(Position pos);

    /**
     * Returns the player index for a given piece.
     * @param piece The piece object
     * @return The player index
     */
    int getPlayerOf(IPiece piece);

    /**
     * Moves a piece from one position to another.
     * @param from The starting position
     * @param to The target position
     */
    void move(Position from, Position to);

    /**
     * Updates all pieces and handles captures and board state.
     */
    void updateAll();

    /**
     * Checks if the specified row and column are within board bounds.
     * @param r The row index
     * @param c The column index
     * @return true if in bounds, false otherwise
     */
    boolean isInBounds(int r, int c);

    /**
     * Checks if the specified position is within board bounds.
     * @param p The position object
     * @return true if in bounds, false otherwise
     */
    boolean isInBounds(Position p);

    /**
     * Checks if a move from one position to another is legal.
     * @param from The starting position
     * @param to The target position
     * @return true if legal, false otherwise
     */
    boolean isMoveLegal(Position from, Position to);

    /**
     * Checks if the path between two positions is clear for movement.
     * @param from The starting position
     * @param to The target position
     * @return true if path is clear, false otherwise
     */
    boolean isPathClear(Position from, Position to);

    /**
     * Checks if a jump action is legal for the given piece.
     * @param p The piece object
     * @return true if jump is legal, false otherwise
     */
    boolean isJumpLegal(IPiece p);

    /**
     * Performs a jump action for the given piece.
     * @param p The piece object
     */
    void jump(IPiece p);

    /**
     * Returns the array of players.
     * @return Array of players
     */
    IPlayer[] getPlayers();

    /**
     * Returns the number of rows on the board.
     * @return Number of rows
     */
    int getROWS();

    /**
     * Returns the number of columns on the board.
     * @return Number of columns
     */
    int getCOLS();

    /**
     * Returns the board configuration.
     * @return BoardConfig object
     */
    BoardConfig getBoardConfig();

    List<Position> getLegalMoves(Position selectedPosition);
}
