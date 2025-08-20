package interfaces;

import board.BoardConfig;
import pieces.Position;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Interface representing a game player and their actions.
 */
public interface IPlayer extends Serializable {

    /**
     * Gets the list of pieces currently owned by this player.
     * @return List of IPiece objects
     */
    List<IPiece> getPieces();

    /**
     * Gets the unique player ID.
     * @return Player ID
     */
    int getId();

    /**
     * Gets the player's display name.
     * @return Player name
     */
    String getName();

    /**
     * Checks if the player has failed or lost (e.g., their king was captured).
     * @return True if player has failed, false otherwise
     */
    boolean isFailed();

    /**
     * Marks the specified piece as captured and updates player state if needed.
     * @param p The piece to mark as captured
     */
    void markPieceCaptured(IPiece p);

    /**
     * Handles a selection event for this player.
     * @param board The game board
     * @param selected The selected position
     * @return Optional ICommand representing the action to execute, empty if no action
     */
    Optional<ICommand> handleSelection(IBoard board, Position selected);

    /**
     * Gets the player's current score.
     * @return Player score
     */
    int getScore();

    /**
     * Replaces a pawn with a queen or another promotion piece.
     * @param piece The piece to promote
     * @param targetPos Target position for the new piece
     * @param bc Board configuration
     * @return The new promoted piece
     */
    IPiece replacePToQ(IPiece piece, Position targetPos, BoardConfig bc);

    /**
     * Gets the player's color for rendering purposes.
     * @return Player color
     */
    Color getColor();

    /**
     * Sets the player's display name.
     * @param name New name
     */
    void setName(String name);
}
