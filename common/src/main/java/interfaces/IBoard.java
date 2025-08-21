package interfaces;

import board.BoardConfig;
import pieces.Position;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing a game board.
 * Provides methods to query pieces, move them, and check board state.
 */
public interface IBoard extends Serializable {

    /**
     * Checks if a piece exists at the given position.
     *
     * @param pos Position to check
     * @return true if a piece exists at the position, false otherwise
     */
    boolean hasPiece(Position pos);

    /**
     * Checks if a piece exists at the given position
     * or if the position is marked as a valid target.
     *
     * @param pos Position to check
     * @return true if occupied or a valid target
     */
    boolean hasPieceOrIsTarget(Position pos);

    /**
     * Returns the piece located at the specified position.
     *
     * @param pos Position to query
     * @return IPiece object at the position, or null if empty
     */
    IPiece getPiece(Position pos);

    /**
     * Returns the player index that owns the given position.
     *
     * @param pos Position to query
     * @return Player index
     */
    int getPlayerOf(Position pos);

    /**
     * Moves a piece from one position to another.
     *
     * @param from Starting position
     * @param to   Target position
     */
    void move(Position from, Position to);

    /**
     * Performs a jump action for the given piece.
     *
     * @param p Piece to jump
     */
    void jump(IPiece p);

    /**
     * Updates all pieces on the board and handles any required board state changes.
     */
    void updateAll();

    /**
     * Checks whether a given position is within the board boundaries.
     *
     * @param p Position to check
     * @return true if within bounds, false otherwise
     */
    boolean isInBounds(Position p);

    /**
     * Returns all players on the board.
     *
     * @return Array of IPlayer objects
     */
    IPlayer[] getPlayers();

    /**
     * Returns the number of rows of the board.
     *
     * @return Row count
     */
    int getRows();

    /**
     * Returns the number of columns of the board.
     *
     * @return Column count
     */
    int getCols();

    /**
     * Returns the board configuration.
     *
     * @return BoardConfig object
     */
    BoardConfig getBoardConfig();

    /**
     * Returns all legal moves available from the given selected position.
     *
     * @param selectedPosition Position of the selected piece
     * @return List of positions representing legal moves
     */
    List<Position> getLegalMoves(Position selectedPosition);

    /**
     * Returns the target state at the given position.
     *
     * @param pos Position to query
     * @return Target value (player index) or indicator of no target
     */
    int getTarget(Position pos);

    /**
     * Sets a piece at the specified position on the board grid.
     *
     * @param pos   Position to place the piece
     * @param piece IPiece object to place
     */
    void setGrid(Position pos, IPiece piece);

    /**
     * Marks the specified position as having no target.
     *
     * @param pos Position to update
     */
    void setIsNoTarget(Position pos);

    /**
     * Returns the board rules engine responsible for move legality and piece actions.
     *
     * @return IBoardEngine instance
     */
    IBoardEngine getBoardRulesEngine();
}
