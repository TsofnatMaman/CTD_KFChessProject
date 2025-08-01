package interfaces;

import board.BoardConfig;
import pieces.Position;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * Interface for player operations.
 */
public interface IPlayer extends Serializable {
    /**
     * Gets the list of pieces owned by the player.
     * @return List of pieces
     */
    List<IPiece> getPieces();

    /**
     * Gets the player's ID.
     * @return The player ID
     */
    int getId();

    String getName();

    /**
     * Gets the pending position for selection.
     * @return The pending position
     */
    Position getPendingFrom();

    /**
     * Sets the pending position for selection.
     * @param pending The pending position
     */
    void setPendingFrom(Position pending);

    /**
     * Returns true if the player has failed (e.g., lost their king).
     * @return true if failed, false otherwise
     */
    boolean isFailed();

    /**
     * Marks a piece as captured and updates player status if king is captured.
     * @param p The piece to mark as captured
     */
    void markPieceCaptured(IPiece p);

    /**
     * Handles the selection logic for the player, returning a command if an action is performed.
     * @param board The game board
     * @return ICommand representing the action, or null if no action
     */

    ICommand handleSelection(IBoard board, Position selected);

    int getScore();

    IPiece replacePToQ(IPiece piece, Position targetPos, BoardConfig bc);

    Color getColor();

    void setName(String name);
}
