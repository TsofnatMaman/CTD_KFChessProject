package interfaces;

import board.BoardConfig;
import pieces.Position;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for board operations and queries.
 */
public interface IBoard extends Serializable {

    boolean hasPiece(Position pos);

    boolean hasPieceOrIsTarget(Position pos);

    /**
     * Gets the piece at the specified position.
     * @param pos The position object
     * @return The piece or null
     */
    IPiece getPiece(Position pos);

    /**
     * Returns the player index for a given position.
     * @param pos The position object
     * @return The player index
     */
    int getPlayerOf(Position pos);

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
    int getRows();

    /**
     * Returns the number of columns on the board.
     * @return Number of columns
     */
    int getCols();

    /**
     * Returns the board configuration.
     * @return BoardConfig object
     */
    BoardConfig getBoardConfig();

    List<Position> getLegalMoves(Position selectedPosition);
}
