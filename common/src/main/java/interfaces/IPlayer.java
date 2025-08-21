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

    /** Returns the list of pieces currently owned by this player. */
    List<IPiece> getPieces();

    /** Returns the unique player ID. */
    int getId();

    /** Returns the player's display name. */
    String getName();

    /** Returns true if the player has failed or lost (e.g., key piece captured). */
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

    /** Returns the player's current score. */
    int getScore();

    /**
     * Promotes a pawn to a queen or another promotion piece.
     * @param piece The piece to promote
     * @param targetPos Target position for the new piece
     * @param bc Board configuration
     * @return The new promoted piece
     */
    IPiece replacePToQ(IPiece piece, Position targetPos, BoardConfig bc);

    /** Returns the player's color for rendering purposes. */
    Color getColor();

    /** Sets the player's display name. */
    void setName(String name);
}
