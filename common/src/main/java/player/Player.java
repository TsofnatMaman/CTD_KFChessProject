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
 * Represents a player in the game, holding pieces and managing actions.
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
    @Override
    public void markPieceCaptured(IPiece p) {
        if (p == null) return;

        p.markCaptured();
        score -= p.getType().getScore();
        if (p.getType() == EPieceType.K) {
            isFailed = true;
        }
    }

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
     * Replaces the given piece with a queen (promotion) and updates score.
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
     * Converts a Color object to a readable string.
     */
    private String colorToString(Color c) {
        if (c == null) return "null";
        return String.format("RGB(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
    }

}
