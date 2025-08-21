package player;

import board.BoardConfig;
import command.JumpCommand;
import command.MoveCommand;
import interfaces.ICommand;
import interfaces.IBoard;
import interfaces.IPiece;
import interfaces.IPlayer;
import pieces.EPieceType;
import pieces.PiecesFactory;
import pieces.Position;
import utils.LogUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Represents a player in the chess game.
 * <p>
 * Each player has an ID, name, color, pieces, and score.
 * The player can select pieces, execute moves, handle promotions, and track captured pieces.
 * </p>
 */
public class Player implements IPlayer {

    private final int id;
    private String name;
    private Position pending;
    private final Color color;
    private final List<IPiece> pieces;
    private int score;
    private boolean isFailed;

    /**
     * Constructs a Player with explicit ID, name, color, and initial pieces.
     *
     * @param id            the player ID
     * @param name          the player name
     * @param color         the player's color
     * @param initialPieces list of initial pieces
     */
    Player(int id, String name, Color color, List<IPiece> initialPieces) {
        if (initialPieces == null) throw new IllegalArgumentException("initialPieces cannot be null");

        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.color = color == null ? Color.WHITE : color;
        this.pieces = new ArrayList<>(initialPieces);
        this.pending = null;
        this.isFailed = false;

        this.score = 0;
        for (IPiece p : pieces) {
            this.score += p.getType().getScore();
        }
    }

    // ===== Getters =====

    @Override
    public List<IPiece> getPieces() {
        return Collections.unmodifiableList(pieces);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public boolean isFailed() {
        return isFailed;
    }

    // ===== Setters =====

    @Override
    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    private Position getPendingFrom() {
        return pending == null ? null : pending.copy();
    }

    private void setPendingFrom(Position pending) {
        this.pending = pending == null ? null : pending.copy();
    }

    // ===== Game Actions =====

    /**
     * Marks a piece as captured and updates score.
     * If the captured piece is a King, marks the player as failed.
     *
     * @param p the piece to mark as captured
     */
    @Override
    public void markPieceCaptured(IPiece p) {
        if (p == null) return;

        p.markCaptured();
        score -= p.getType().getScore();
        if (p.getType() == EPieceType.K) {
            isFailed = true;
        }
    }

    /**
     * Handles the player's selection on the board.
     * <p>
     * First click selects a piece; second click either moves or jumps the piece.
     * Returns an ICommand representing the move or jump if valid.
     * </p>
     *
     * @param board    the game board
     * @param selected the position selected by the player
     * @return an optional command to execute, empty if selection is invalid
     */
    @Override
    public Optional<ICommand> handleSelection(IBoard board, Position selected) {
        Position previous = getPendingFrom();

        if (previous == null) {
            IPiece piece = board.getPiece(selected);
            if (piece == null || piece.getPlayer() != id) {
                return Optional.empty();
            }

            if (board.hasPiece(selected) && piece.canAction()) {
                setPendingFrom(selected);
            } else {
                LogUtils.logDebug("Cannot choose piece at " + selected + " for player " + id);
            }

        } else {
            setPendingFrom(null);

            if (previous.equals(selected)) {
                IPiece piece = board.getPiece(selected);
                if (piece != null) {
                    return Optional.of(new JumpCommand(piece, board));
                }

            } else {
                return Optional.of(new MoveCommand(previous, selected.copy(), board));
            }
        }

        return Optional.empty();
    }

    /**
     * Replaces a pawn with a queen (promotion) at the given position and updates score.
     *
     * @param piece     the piece to promote
     * @param targetPos the target position for the new queen
     * @param bc        the board configuration
     * @return the new queen piece
     */
    @Override
    public IPiece replacePToQ(IPiece piece, Position targetPos, BoardConfig bc) {
        if (piece == null) throw new IllegalArgumentException("piece cannot be null");

        pieces.remove(piece);
        score -= piece.getType().getScore();

        IPiece queen = PiecesFactory.createPieceByCode(
                EPieceType.Q,
                id,
                targetPos,
                bc
        );

        if (queen != null) {
            pieces.add(queen);
            score += queen.getType().getScore();
        } else {
            LogUtils.logDebug("Failed to promote piece to Queen at " + targetPos + " for player " + id);
        }

        return queen;
    }

    @Override
    public String toString() {
        long activePieces = pieces.stream().filter(p -> !p.isCaptured()).count();

        return String.format(
                "Player{id=%d, name='%s', color=%s, score=%d, failed=%b, activePieces=%d}",
                id,
                name,
                colorToString(color),
                score,
                isFailed,
                activePieces
        );
    }

    /**
     * Converts a Color object to a readable RGB string.
     *
     * @param c the color to convert
     * @return string representation of the color
     */
    private String colorToString(Color c) {
        if (c == null) return "null";
        return String.format("RGB(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
    }
}
